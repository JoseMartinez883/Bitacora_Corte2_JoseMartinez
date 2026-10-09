package com.restaurante.model.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ResenaResponseDTO(
        String id,
        UUID idPedido,
        String nombreCliente,
        int calificacion,
        String comentario,
        LocalDateTime fechaCreacion
) {}
