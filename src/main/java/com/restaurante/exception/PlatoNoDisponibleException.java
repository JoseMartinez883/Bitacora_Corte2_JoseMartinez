package com.restaurante.exception;

public class PlatoNoDisponibleException extends RuntimeException {
    public PlatoNoDisponibleException(Long id) {
        super("El plato con id " + id + " no está disponible o está marcado como agotado.");
    }
}
