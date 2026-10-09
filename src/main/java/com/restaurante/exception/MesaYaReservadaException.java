package com.restaurante.exception;

import java.time.LocalDateTime;

public class MesaYaReservadaException extends RuntimeException {
    public MesaYaReservadaException(Long idMesa, LocalDateTime fechaHora) {
        super(String.format("La mesa con ID %d ya cuenta con una reserva activa para el horario %s.", idMesa, fechaHora));
    }
}
