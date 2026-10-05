package com.restaurante.mapper;

import com.restaurante.model.domain.Mesa;
import com.restaurante.model.dto.response.MesaResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MesaMapperOut {

    MesaResponseDTO toResponse(Mesa mesa);
}
