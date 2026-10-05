package com.restaurante.mapper;

import com.restaurante.model.domain.RegistroVehiculo;
import com.restaurante.model.dto.request.RegistroVehiculoRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RegistroVehiculoMapperIn {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "salida", ignore = true)
    @Mapping(target = "cobro", ignore = true)
    @Mapping(target = "placa", expression = "java(dto.placa() != null ? dto.placa().toUpperCase() : null)")
    @Mapping(target = "entrada", expression = "java(java.time.LocalDateTime.now(java.time.ZoneId.systemDefault()))")
    @Mapping(target = "estado", constant = "ACTIVO")
    RegistroVehiculo toDomain(RegistroVehiculoRequestDTO dto);
}
