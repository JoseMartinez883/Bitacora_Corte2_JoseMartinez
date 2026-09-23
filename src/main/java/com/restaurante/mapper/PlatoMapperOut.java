package com.restaurante.mapper;

import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.response.PlatoResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class PlatoMapperOut {

    public PlatoResponseDTO toResponse(Plato plato) {
        return new PlatoResponseDTO(
                plato.getId(),
                plato.getNombre(),
                plato.getPrecio(),
                plato.getCategoria(),
                plato.getMasa(),
                plato.getSalsa(),
                plato.getToppings(),
                plato.getDescripcion(),
                plato.isDisponible(),
                plato.isActivo()
        );
    }
}
