package com.restaurante.mapper;

import com.restaurante.model.domain.RegistroVehiculo;
import com.restaurante.model.dto.request.RegistroVehiculoRequestDTO;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class RegistroVehiculoMapperIn {

    public RegistroVehiculo toDomain(RegistroVehiculoRequestDTO dto) {
        RegistroVehiculo registro = new RegistroVehiculo();
        registro.setPlaca(dto.placa().toUpperCase());
        registro.setEntrada(LocalDateTime.now());
        registro.setEstado("ACTIVO");
        return registro;
    }
}
