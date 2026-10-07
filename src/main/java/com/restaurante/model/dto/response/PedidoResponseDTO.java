package com.restaurante.model.dto.response;

import com.restaurante.model.domain.EstadoPedido;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PedidoResponseDTO(
        UUID id,
        Long idMesa,
        EstadoPedido estado,
        List<ItemPedidoResponseDTO> items,
        LocalDateTime timestamp
) {}
