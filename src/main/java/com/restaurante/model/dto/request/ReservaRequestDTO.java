package com.restaurante.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

public record ReservaRequestDTO(
        @NotNull Long idMesa,
        @NotBlank(message = "El nombre del cliente es obligatorio.") String cliente,
        @NotNull(message = "La fecha y hora son obligatorias.") LocalDateTime fechaHora,
        @Positive(message = "El número de comensales debe ser mayor a 0.") Integer comensales
) {}
