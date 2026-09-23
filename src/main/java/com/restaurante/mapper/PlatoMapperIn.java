package com.restaurante.mapper;

import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.request.PlatoRequestDTO;
import org.springframework.stereotype.Component;
import java.util.ArrayList;

@Component
public class PlatoMapperIn {

    public Plato toDomain(PlatoRequestDTO dto) {
        Plato plato = new Plato();
        plato.setNombre(dto.nombre());
        plato.setPrecio(dto.precio());
        plato.setCategoria(dto.categoria());
        plato.setMasa(dto.masa());
        plato.setSalsa(dto.salsa());
        plato.setToppings(dto.toppings() != null ? dto.toppings() : new ArrayList<>());
        plato.setDescripcion(dto.descripcion());
        plato.setDisponible(true);
        plato.setActivo(true);
        return plato;
    }
}
