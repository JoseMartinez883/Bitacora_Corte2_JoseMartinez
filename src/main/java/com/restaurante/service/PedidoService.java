package com.restaurante.service;

import com.restaurante.model.dto.request.PedidoRequestDTO;
import com.restaurante.model.dto.request.CambioEstadoRequestDTO;
import com.restaurante.model.dto.response.PedidoResponseDTO;
import com.restaurante.model.domain.EstadoPedido;
import java.util.List;

import java.util.UUID;

public interface PedidoService {
    PedidoResponseDTO crearPedido(PedidoRequestDTO dto);
    PedidoResponseDTO obtenerPedidoPorId(UUID id);
    List<PedidoResponseDTO> listarTodos();
    List<PedidoResponseDTO> listarPorEstado(EstadoPedido estado);
    PedidoResponseDTO cambiarEstado(UUID id, CambioEstadoRequestDTO dto);
    void eliminar(UUID id);
    PedidoResponseDTO eliminarItemPedido(UUID idPedido, Long idItem);
}
