package com.restaurante.mapper;

import com.restaurante.model.domain.Reserva;
import com.restaurante.model.dto.request.ReservaRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservaMapperIn {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activa", constant = "true")
    Reserva toDomain(ReservaRequestDTO dto);
}
