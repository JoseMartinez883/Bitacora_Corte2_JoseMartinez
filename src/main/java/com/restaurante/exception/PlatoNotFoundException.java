package com.restaurante.exception;

public class PlatoNotFoundException extends RuntimeException {
    public PlatoNotFoundException(Long id) {
        super("Plato con id " + id + " no encontrado.");
    }
    public PlatoNotFoundException(String mensaje) {
        super(mensaje);
    }
}

