package com.restaurante.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

public record PlatoRequestDTO(
        @NotBlank(message = "El nombre del plato es obligatorio.")
        String nombre,

        @Positive(message = "El precio debe ser mayor a 0.")
        Double precio,

        @NotBlank(message = "La categoría es obligatoria.")
        String categoria,

        @NotBlank(message = "La masa base es obligatoria.")
        String masa,

        @NotBlank(message = "La salsa primaria es obligatoria.")
        String salsa,

        @Size(max = 5, message = "Máximo 5 toppings.")
        List<String> toppings,

        String descripcion
) {}
