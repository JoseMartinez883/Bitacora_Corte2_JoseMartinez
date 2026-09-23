package com.restaurante.model.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ItemPedidoRequestDTO(
        @NotNull(message = "El id del plato es obligatorio.")
        Long idPlato,

        @Positive(message = "La cantidad debe ser mayor a 0.")
        Integer cantidad,

        @Size(max = 5, message = "Máximo 5 toppings por ítem.")
        List<String> toppings
) {}
