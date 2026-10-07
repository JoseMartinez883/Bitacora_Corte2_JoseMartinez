package com.restaurante.exception;

import java.util.UUID;

public class ReservaNotFoundException extends RuntimeException {
    public ReservaNotFoundException(UUID id) {
        super("Reserva con id " + id + " no encontrada.");
    }
}
