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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepositoryJPA pedidoRepository;
    private final PlatoRepositoryJPA platoRepository;
    private final EventoPedidoRepositoryMongo eventoMongoRepository;
    private final PedidoEntityMapper pedidoEntityMapper;
    private final PlatoEntityMapper platoEntityMapper;
    private final PedidoMapperIn pedidoMapperIn;
    private final PedidoMapperOut pedidoMapperOut;
    private final PlatoValidator platoValidator;

    @Override
    public PedidoResponseDTO crearPedido(PedidoRequestDTO dto) {
        List<Plato> platos = new ArrayList<>();
        for (var item : dto.items()) {
            Plato plato = platoRepository.findById(item.idPlato()).map(platoEntityMapper::toDomain).orElseThrow(() -> new PlatoNotFoundException(item.idPlato()));
            platoValidator.validarDisponibilidad(plato);
            platos.add(plato);
        }
        Pedido pedido = pedidoMapperIn.toDomain(dto, platos);
        Pedido guardado = pedidoEntityMapper.toDomain(pedidoRepository.save(pedidoEntityMapper.toEntity(pedido)));
        guardarEventoMongo(guardado.getId(), "N/A", guardado.getEstado().name(), "SISTEMA");
        return pedidoMapperOut.toResponse(guardado);
    }

    @Override
    public PedidoResponseDTO obtenerPedidoPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id).map(pedidoEntityMapper::toDomain).orElseThrow(() -> new PedidoNotFoundException(id));
        return pedidoMapperOut.toResponse(pedido);
    }

    @Override
    public List<PedidoResponseDTO> listarTodos() {
        return pedidoRepository.findAll().stream().map(pedidoEntityMapper::toDomain).map(pedidoMapperOut::toResponse).toList();
    }

    @Override
    public List<PedidoResponseDTO> listarPorEstado(EstadoPedido estado) {
        return pedidoRepository.findAll().stream().filter(p -> p.getEstado() == estado).map(pedidoEntityMapper::toDomain).map(pedidoMapperOut::toResponse).toList();
    }

    @Override
    public PedidoResponseDTO cambiarEstado(Long id, CambioEstadoRequestDTO dto) {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        boolean isChef = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CHEF"));
        boolean isMesero = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MESERO"));
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (dto.estadoDestino() == EstadoPedido.EN_PREPARACION || dto.estadoDestino() == EstadoPedido.LISTO) {
            if (!isChef && !isAdmin) throw new org.springframework.security.access.AccessDeniedException("Solo el Chef puede cambiar a este estado.");
        }
        if (dto.estadoDestino() == EstadoPedido.ENTREGADO) {
            if (!isMesero && !isAdmin) throw new org.springframework.security.access.AccessDeniedException("Solo el Mesero puede entregar el pedido.");
        }

        Pedido pedido = pedidoRepository.findById(id).map(pedidoEntityMapper::toDomain).orElseThrow(() -> new PedidoNotFoundException(id));
        String estAnt = pedido.getEstado().name();
        pedido.cambiarEstado(dto.estadoDestino());
        Pedido guardado = pedidoEntityMapper.toDomain(pedidoRepository.save(pedidoEntityMapper.toEntity(pedido)));
        guardarEventoMongo(guardado.getId(), estAnt, guardado.getEstado().name(), "Operario_" + dto.idOperario());
        return pedidoMapperOut.toResponse(guardado);
    }

    @Override
    public void eliminar(Long id) {
        if (!pedidoRepository.existsById(id)) throw new PedidoNotFoundException(id);
        pedidoRepository.deleteById(id);
    }

    private void guardarEventoMongo(Long idPedido, String estadoAnterior, String estadoNuevo, String usuario) {
        EventoPedidoDocument evento = EventoPedidoDocument.builder().idPedido(idPedido).estadoAnterior(estadoAnterior).estadoNuevo(estadoNuevo).usuarioQueCambio(usuario).timestamp(LocalDateTime.now(java.time.ZoneId.systemDefault())).build();
        eventoMongoRepository.save(evento);
    }
}
