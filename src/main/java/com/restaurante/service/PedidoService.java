package com.restaurante.service;

import com.restaurante.model.dto.request.PedidoRequestDTO;
import com.restaurante.model.dto.request.CambioEstadoRequestDTO;
import com.restaurante.model.dto.response.PedidoResponseDTO;
import com.restaurante.model.domain.EstadoPedido;
import java.util.List;

public interface PedidoService {
    PedidoResponseDTO crearPedido(PedidoRequestDTO dto);
    PedidoResponseDTO obtenerPedidoPorId(Long id);
    List<PedidoResponseDTO> listarTodos();
    List<PedidoResponseDTO> listarPorEstado(EstadoPedido estado);
    PedidoResponseDTO cambiarEstado(Long id, CambioEstadoRequestDTO dto);
}
