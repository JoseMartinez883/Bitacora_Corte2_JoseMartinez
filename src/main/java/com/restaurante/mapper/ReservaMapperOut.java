package com.restaurante.mapper;

import com.restaurante.model.domain.Reserva;
import com.restaurante.model.dto.response.ReservaResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReservaMapperOut {

    ReservaResponseDTO toResponse(Reserva reserva);
}
