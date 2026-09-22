package com.restaurante.exception;

public class PlatoAlreadyExistException extends RuntimeException {
    public PlatoAlreadyExistException(String nombre) {
        super("Ya existe un plato con el nombre: " + nombre);
    }
}

