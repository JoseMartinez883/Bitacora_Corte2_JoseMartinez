package com.restaurante.exception;

import com.restaurante.model.domain.EstadoPedido;

public class TransicionEstadoInvalidaException extends RuntimeException {
    public TransicionEstadoInvalidaException(EstadoPedido actual, EstadoPedido destino) {
        super("Transición inválida: no se puede pasar de " + actual + " a " + destino + ".");
    }
}
