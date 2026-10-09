package com.restaurante.service;

import com.restaurante.exception.PedidoNotFoundException;
import com.restaurante.exception.PlatoNotFoundException;
import com.restaurante.mapper.PedidoEntityMapper;
import com.restaurante.mapper.PlatoEntityMapper;
import com.restaurante.mapper.PedidoMapperIn;
import com.restaurante.mapper.PedidoMapperOut;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.Pedido;
import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.request.CambioEstadoRequestDTO;
import com.restaurante.model.dto.request.PedidoRequestDTO;
import com.restaurante.model.dto.response.PedidoResponseDTO;
import com.restaurante.persistence.document.EventoPedidoDocument;
import com.restaurante.repository.EventoPedidoRepositoryMongo;
import com.restaurante.repository.PedidoRepositoryJPA;
import com.restaurante.repository.PlatoRepositoryJPA;
import com.restaurante.validator.PlatoValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepositoryJPA pedidoRepository;
    private final PlatoRepositoryJPA platoRepository;
    private final com.restaurante.repository.IngredienteRepositoryJPA ingredienteRepository;
    private final EventoPedidoRepositoryMongo eventoMongoRepository;
    private final PedidoEntityMapper pedidoEntityMapper;
    private final PlatoEntityMapper platoEntityMapper;
    private final PedidoMapperIn pedidoMapperIn;
    private final PedidoMapperOut pedidoMapperOut;
    private final PlatoValidator platoValidator;

    @Override
    @Transactional
    public PedidoResponseDTO crearPedido(PedidoRequestDTO dto) {
        List<Plato> platos = new ArrayList<>();
        for (var item : dto.items()) {
            Plato plato = platoRepository.findById(item.idPlato()).map(platoEntityMapper::toDomain).orElseThrow(() -> new PlatoNotFoundException(item.idPlato()));
            platoValidator.validarDisponibilidad(plato);
            
            // Descontar Inventario
            descontarInventario(plato.getMasa());
            descontarInventario(plato.getSalsa());
            if (plato.getToppings() != null) plato.getToppings().forEach(this::descontarInventario);
            if (plato.getProteinas() != null) plato.getProteinas().forEach(this::descontarInventario);
            if (plato.getSalsasExtras() != null) plato.getSalsasExtras().forEach(this::descontarInventario);

            platos.add(plato);
        }
        Pedido pedido = pedidoMapperIn.toDomain(dto, platos);
        Pedido guardado = pedidoEntityMapper.toDomain(pedidoRepository.save(pedidoEntityMapper.toEntity(pedido)));
        guardarEventoMongo(guardado.getId(), "N/A", guardado.getEstado().name(), "SISTEMA");
        log.info("Pedido creado exitosamente con ID: {} para la mesa: {}", guardado.getId(), guardado.getIdMesa());
        return pedidoMapperOut.toResponse(guardado);
    }

    @Override
    public PedidoResponseDTO obtenerPedidoPorId(UUID id) {
        Pedido pedido = pedidoRepository.findById(id).map(pedidoEntityMapper::toDomain).orElseThrow(() -> new PedidoNotFoundException(id));
        return pedidoMapperOut.toResponse(pedido);
    }

    @Override
    public List<PedidoResponseDTO> listarTodos() {
        return pedidoRepository.findAll().stream().map(pedidoEntityMapper::toDomain).map(pedidoMapperOut::toResponse).toList();
    }

    @Override
    public List<PedidoResponseDTO> listarPorEstado(EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado).stream().map(pedidoEntityMapper::toDomain).map(pedidoMapperOut::toResponse).toList();
    }

    @Override
    @Transactional
    public PedidoResponseDTO cambiarEstado(UUID id, CambioEstadoRequestDTO dto) {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        boolean isChef = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CHEF"));
        boolean isMesero = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MESERO"));
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if ((dto.estadoDestino() == EstadoPedido.EN_PREPARACION || dto.estadoDestino() == EstadoPedido.LISTO) && (!isChef && !isAdmin)) {
            throw new org.springframework.security.access.AccessDeniedException("Solo el Chef puede cambiar a este estado.");
        }
        if (dto.estadoDestino() == EstadoPedido.ENTREGADO && (!isMesero && !isAdmin)) {
            throw new org.springframework.security.access.AccessDeniedException("Solo el Mesero puede entregar el pedido.");
        }

        Pedido pedido = pedidoRepository.findById(id).map(pedidoEntityMapper::toDomain).orElseThrow(() -> new PedidoNotFoundException(id));
        String estAnt = pedido.getEstado().name();
        pedido.cambiarEstado(dto.estadoDestino());
        Pedido guardado = pedidoEntityMapper.toDomain(pedidoRepository.save(pedidoEntityMapper.toEntity(pedido)));
        guardarEventoMongo(guardado.getId(), estAnt, guardado.getEstado().name(), "Operario_" + dto.idOperario());
        log.info("Estado del pedido {} cambiado de {} a {} por operario {}", id, estAnt, guardado.getEstado().name(), dto.idOperario());
        return pedidoMapperOut.toResponse(guardado);
    }

    @Override
    public void eliminar(UUID id) {
        log.info("Iniciando eliminación/cancelación de pedido con ID: {}", id);
        var entity = pedidoRepository.findById(id).orElseThrow(() -> {
            log.warn("No se puede eliminar: pedido con ID {} no encontrado", id);
            return new PedidoNotFoundException(id);
        });
        if (entity.getEstado() != EstadoPedido.RECIBIDO) {
            log.warn("No se puede eliminar pedido {}: estado actual es {} (ya pasó a cocina o fue entregado)", id, entity.getEstado());
            throw new com.restaurante.exception.PedidoEnCocinaException(entity.getEstado());
        }
        pedidoRepository.deleteById(id);
        log.info("Pedido con ID {} cancelado y eliminado exitosamente", id);
    }

    private void guardarEventoMongo(UUID idPedido, String estadoAnterior, String estadoNuevo, String usuario) {
        EventoPedidoDocument evento = EventoPedidoDocument.builder().idPedido(idPedido).estadoAnterior(estadoAnterior).estadoNuevo(estadoNuevo).usuarioQueCambio(usuario).timestamp(LocalDateTime.now(java.time.ZoneId.systemDefault())).build();
        eventoMongoRepository.save(evento);
    }

    private void descontarInventario(String nombreIngrediente) {
        if (nombreIngrediente == null || nombreIngrediente.isBlank()) return;
        ingredienteRepository.findByNombre(nombreIngrediente).ifPresent(ing -> {
            ing.restarStock(1);
            ingredienteRepository.save(ing);
            if (!ing.isDisponible()) {
                desactivarPlatosAsociados(ing.getNombre());
            }
        });
    }

    private void desactivarPlatosAsociados(String ingrediente) {
        // En una app real esto podria ser una query nativa compleja.
        platoRepository.findAll().forEach(p -> {
            boolean contiene = (p.getMasa() != null && p.getMasa().equalsIgnoreCase(ingrediente))
                    || (p.getSalsa() != null && p.getSalsa().equalsIgnoreCase(ingrediente))
                    || (p.getToppings() != null && p.getToppings().contains(ingrediente))
                    || (p.getProteinas() != null && p.getProteinas().contains(ingrediente))
                    || (p.getSalsasExtras() != null && p.getSalsasExtras().contains(ingrediente));
            if (contiene) {
                p.setDisponible(false);
                platoRepository.save(p);
            }
        });
    }

    @Override
    @Transactional
    public PedidoResponseDTO eliminarItemPedido(UUID idPedido, Long idItem) {
        Pedido pedido = pedidoRepository.findById(idPedido).map(pedidoEntityMapper::toDomain).orElseThrow(() -> new PedidoNotFoundException(idPedido));
        if (pedido.getEstado() != EstadoPedido.RECIBIDO) {
            throw new IllegalStateException("Solo se pueden eliminar ítems cuando el pedido está RECIBIDO.");
        }
        
        com.restaurante.persistence.entity.PedidoEntity entity = pedidoRepository.findById(idPedido).get();
        boolean removed = entity.getItems().removeIf(item -> item.getId().equals(idItem));
        if (!removed) {
            throw new IllegalArgumentException("Ítem no encontrado en el pedido.");
        }
        
        log.info("Ítem {} eliminado correctamente del pedido {}", idItem, idPedido);
        // Re-mapeamos para retornar
        return pedidoMapperOut.toResponse(pedidoEntityMapper.toDomain(pedidoRepository.save(entity)));
    }
}
