package com.restaurante.mapper;

import com.restaurante.model.domain.RegistroVehiculo;
import com.restaurante.persistence.entity.RegistroVehiculoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RegistroVehiculoEntityMapper {
    RegistroVehiculoEntity toEntity(RegistroVehiculo domain);
    RegistroVehiculo toDomain(RegistroVehiculoEntity entity);
}
