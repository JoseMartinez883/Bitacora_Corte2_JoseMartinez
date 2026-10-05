package com.restaurante.mapper;

import com.restaurante.model.domain.Plato;
import com.restaurante.persistence.entity.PlatoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlatoEntityMapper {

    PlatoEntity toEntity(Plato domain);

    Plato toDomain(PlatoEntity entity);
}
