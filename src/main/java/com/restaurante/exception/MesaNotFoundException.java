package com.restaurante.exception;

public class MesaNotFoundException extends RuntimeException {
    public MesaNotFoundException(Long id) {
        super("Mesa con id " + id + " no encontrada.");
    }
}
