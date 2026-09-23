package com.restaurante.model.dto.response;

import com.restaurante.model.domain.EstadoPedido;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponseDTO(
        Long id,
        Long idMesa,
        EstadoPedido estado,
        List<ItemPedidoResponseDTO> items,
        LocalDateTime timestamp
) {}
