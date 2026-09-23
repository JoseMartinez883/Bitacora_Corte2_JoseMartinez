package com.restaurante.mapper;

import com.restaurante.model.domain.RegistroVehiculo;
import com.restaurante.model.dto.response.RegistroVehiculoResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class RegistroVehiculoMapperOut {

    public RegistroVehiculoResponseDTO toResponse(RegistroVehiculo registro) {
        return new RegistroVehiculoResponseDTO(
                registro.getId(),
                registro.getPlaca(),
                registro.getEntrada(),
                registro.getSalida(),
                registro.getEstado(),
                registro.getCobro()
        );
    }
}
