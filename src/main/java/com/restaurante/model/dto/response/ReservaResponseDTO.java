package com.restaurante.model.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservaResponseDTO(
        UUID id,
        Long idMesa,
        String cliente,
        LocalDateTime fechaHora,
        Integer comensales,
        boolean activa
) {}
