package com.restaurante.mapper;

import com.restaurante.model.domain.Reserva;
import com.restaurante.model.dto.response.ReservaResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ReservaMapperOut {

    public ReservaResponseDTO toResponse(Reserva reserva) {
        return new ReservaResponseDTO(
                reserva.getId(),
                reserva.getIdMesa(),
                reserva.getCliente(),
                reserva.getFechaHora(),
                reserva.getComensales(),
                reserva.isActiva()
        );
    }
}
