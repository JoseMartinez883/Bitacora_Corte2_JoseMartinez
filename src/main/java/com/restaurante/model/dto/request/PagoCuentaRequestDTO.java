package com.restaurante.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record PagoCuentaRequestDTO(
        @NotBlank(message = "El método de pago es obligatorio.")
        String metodoPago,

        @Positive(message = "El monto recibido debe ser mayor a 0.")
        Double montoRecibido
) {}
