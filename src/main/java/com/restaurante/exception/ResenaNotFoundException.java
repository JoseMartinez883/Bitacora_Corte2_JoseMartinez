package com.restaurante.exception;

public class ResenaNotFoundException extends RuntimeException {
    public ResenaNotFoundException(String id) {
        super("Reseña con id " + id + " no encontrada.");
    }
}
