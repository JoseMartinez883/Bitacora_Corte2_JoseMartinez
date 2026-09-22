package com.restaurante.exception;

public class ReservaNotFoundException extends RuntimeException {
    public ReservaNotFoundException(Long id) {
        super("Reserva con id " + id + " no encontrada.");
    }
}
