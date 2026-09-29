package com.restaurante.service;

import com.restaurante.exception.PedidoNotFoundException;
import com.restaurante.exception.PlatoNotFoundException;
import com.restaurante.mapper.PedidoMapperIn;
import com.restaurante.mapper.PedidoMapperOut;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.Pedido;
import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.request.CambioEstadoRequestDTO;
import com.restaurante.model.dto.request.PedidoRequestDTO;
import com.restaurante.model.dto.response.PedidoResponseDTO;
import com.restaurante.repository.PedidoRepository;
import com.restaurante.repository.PlatoRepository;
import com.restaurante.validator.PlatoValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PlatoRepository platoRepository;
    private final PedidoMapperIn pedidoMapperIn;
    private final PedidoMapperOut pedidoMapperOut;
    private final PlatoValidator platoValidator;

    @Override
    public PedidoResponseDTO crearPedido(PedidoRequestDTO dto) {
        log.info("Creando pedido para mesa: {}", dto.idMesa());
        List<Plato> platos = new ArrayList<>();
        for (var item : dto.items()) {
            Plato plato = platoRepository.buscarPorId(item.idPlato())
                    .orElseThrow(() -> new PlatoNotFoundException(item.idPlato()));
            platoValidator.validarDisponibilidad(plato); // RF11
            platos.add(plato);
        }
        Pedido pedido = pedidoMapperIn.toDomain(dto, platos);
        Pedido guardado = pedidoRepository.guardar(pedido);
        log.info("Pedido {} creado en estado {}", guardado.getId(), guardado.getEstado());
        return pedidoMapperOut.toResponse(guardado);
    }

    @Override
    public PedidoResponseDTO obtenerPedidoPorId(Long id) {
        Pedido pedido = pedidoRepository.buscarPorId(id)
                .orElseThrow(() -> new PedidoNotFoundException(id));
        return pedidoMapperOut.toResponse(pedido);
    }

    @Override
    public List<PedidoResponseDTO> listarTodos() {
        return pedidoRepository.buscarTodos().stream()
                .map(pedidoMapperOut::toResponse)
                .toList();
    }

    @Override
    public List<PedidoResponseDTO> listarPorEstado(EstadoPedido estado) {
        log.info("Tablero cocina — filtrando por estado: {}", estado);
        return pedidoRepository.buscarPorEstado(estado).stream()
                .map(pedidoMapperOut::toResponse)
                .toList();
    }

    @Override
    public PedidoResponseDTO cambiarEstado(Long id, CambioEstadoRequestDTO dto) {
        log.info("Operario {} cambia pedido {} a estado {}", dto.idOperario(), id, dto.estadoDestino());
        Pedido pedido = pedidoRepository.buscarPorId(id)
                .orElseThrow(() -> new PedidoNotFoundException(id));
        pedido.cambiarEstado(dto.estadoDestino()); // RN-04: validación en dominio
        return pedidoMapperOut.toResponse(pedidoRepository.guardar(pedido));
    }

    @Override
    public void eliminar(Long id) {
        log.info("Eliminando pedido con id: {}", id);
        pedidoRepository.buscarPorId(id);
        pedidoRepository.eliminar(id);
    }

}
