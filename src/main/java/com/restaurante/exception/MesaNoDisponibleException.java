package com.restaurante.exception;

public class MesaNoDisponibleException extends RuntimeException {
    public MesaNoDisponibleException(Long idMesa) {
        super("La mesa " + idMesa + " ya tiene una cuenta abierta o no está disponible.");
    }
}

