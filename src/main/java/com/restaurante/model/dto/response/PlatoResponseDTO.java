package com.restaurante.model.dto.response;

import java.util.List;

public record PlatoResponseDTO(
        Long id,
        String nombre,
        Double precio,
        String categoria,
        String masa,
        String salsa,
        List<String> toppings,
        String descripcion,
        boolean disponible,
        boolean activo
) {}
