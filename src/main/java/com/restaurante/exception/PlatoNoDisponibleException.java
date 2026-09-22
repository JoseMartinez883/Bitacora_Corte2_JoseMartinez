package com.restaurante.exception;

public class PlatoNoDisponibleException extends RuntimeException {
    public PlatoNoDisponibleException(String nombre) {
        super("El plato '" + nombre + "' no está disponible por agotamiento de ingredientes.");
    }
}
