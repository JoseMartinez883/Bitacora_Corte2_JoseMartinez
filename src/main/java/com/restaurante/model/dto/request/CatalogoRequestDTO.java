package com.restaurante.model.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CatalogoRequestDTO(
        @NotNull(message = "El id del plato es obligatorio.") Long idPlato,
        @NotEmpty(message = "Debe incluir al menos una imagen.") List<String> imagenesUrls,
        List<String> etiquetasComerciales
) {}
