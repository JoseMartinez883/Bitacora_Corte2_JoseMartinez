package com.restaurante.model.dto.response;

import java.util.List;

public record CatalogoResponseDTO(
        String id,
        Long idPlato,
        List<String> imagenesUrls,
        List<String> etiquetasComerciales
) {}
