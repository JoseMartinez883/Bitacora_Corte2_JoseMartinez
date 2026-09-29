package com.restaurante.mapper;

import com.restaurante.model.domain.Plato;
import com.restaurante.persistence.entity.PlatoEntity;
import org.springframework.stereotype.Component;

@Component
public class PlatoEntityMapper {

    public PlatoEntity toEntity(Plato domain) {
        if (domain == null) return null;
        PlatoEntity entity = new PlatoEntity();
        entity.setId(domain.getId());
        entity.setNombre(domain.getNombre());
        entity.setPrecio(domain.getPrecio());
        entity.setCategoria(domain.getCategoria());
        entity.setDisponible(domain.isDisponible());
        entity.setDescripcion(domain.getDescripcion());
        entity.setMasa(domain.getMasa());
        entity.setSalsa(domain.getSalsa());
        entity.setToppings(domain.getToppings());
        entity.setActivo(domain.isActivo());
        return entity;
    }

    public Plato toDomain(PlatoEntity entity) {
        if (entity == null) return null;
        Plato domain = new Plato();
        domain.setId(entity.getId());
        domain.setNombre(entity.getNombre());
        domain.setPrecio(entity.getPrecio());
        domain.setCategoria(entity.getCategoria());
        domain.setDisponible(entity.isDisponible());
        domain.setDescripcion(entity.getDescripcion());
        domain.setMasa(entity.getMasa());
        domain.setSalsa(entity.getSalsa());
        domain.setToppings(entity.getToppings());
        domain.setActivo(entity.isActivo());
        return domain;
    }
}
