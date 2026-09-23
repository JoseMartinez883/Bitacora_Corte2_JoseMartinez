package com.restaurante.mapper;

import com.restaurante.model.domain.Mesa;
import com.restaurante.model.dto.response.MesaResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class MesaMapperOut {

    public MesaResponseDTO toResponse(Mesa mesa) {
        return new MesaResponseDTO(
                mesa.getId(),
                mesa.getNumero(),
                mesa.getCapacidad(),
                mesa.getEstado(),
                mesa.isCuentaAbierta()
        );
    }
}
