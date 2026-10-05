package com.restaurante.mapper;

import com.restaurante.model.domain.RegistroVehiculo;
import com.restaurante.model.dto.response.RegistroVehiculoResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RegistroVehiculoMapperOut {

    RegistroVehiculoResponseDTO toResponse(RegistroVehiculo registro);
}
