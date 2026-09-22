package com.restaurante.exception;

public class PedidoNotFoundException extends RuntimeException {
    public PedidoNotFoundException(Long id) {
        super("Pedido con id " + id + " no encontrado.");
    }
}
