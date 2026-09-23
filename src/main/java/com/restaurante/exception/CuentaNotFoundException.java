package com.restaurante.exception;

public class CuentaNotFoundException extends RuntimeException {
    public CuentaNotFoundException(Long idMesa) {
        super("No existe una cuenta activa para la mesa con id " + idMesa + ".");
    }
}
