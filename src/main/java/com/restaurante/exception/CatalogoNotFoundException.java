package com.restaurante.exception;

public class CatalogoNotFoundException extends RuntimeException {
    public CatalogoNotFoundException(String mensaje) {
        super(mensaje);
    }
}
