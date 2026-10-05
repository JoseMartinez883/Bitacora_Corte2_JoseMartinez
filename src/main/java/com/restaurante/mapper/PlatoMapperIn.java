package com.restaurante.mapper;

import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.request.PlatoRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlatoMapperIn {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "toppings", expression = "java(dto.toppings() != null ? dto.toppings() : new java.util.ArrayList<>())")
    @Mapping(target = "disponible", constant = "true")
    @Mapping(target = "activo", constant = "true")
    Plato toDomain(PlatoRequestDTO dto);
}
