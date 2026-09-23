package com.restaurante.mapper;

import com.restaurante.model.domain.Reserva;
import com.restaurante.model.dto.request.ReservaRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class ReservaMapperIn {

    public Reserva toDomain(ReservaRequestDTO dto) {
        Reserva reserva = new Reserva();
        reserva.setIdMesa(dto.idMesa());
        reserva.setCliente(dto.cliente());
        reserva.setFechaHora(dto.fechaHora());
        reserva.setComensales(dto.comensales());
        reserva.setActiva(true);
        return reserva;
    }
}
