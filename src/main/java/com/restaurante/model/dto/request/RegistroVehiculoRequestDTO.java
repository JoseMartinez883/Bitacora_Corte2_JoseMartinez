package com.restaurante.model.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RegistroVehiculoRequestDTO(
        @NotBlank(message = "La placa del vehículo es obligatoria.") String placa
) {}
