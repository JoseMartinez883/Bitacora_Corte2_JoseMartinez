package com.restaurante.exception;

import java.util.UUID;

public class PedidoNotFoundException extends RuntimeException {
    public PedidoNotFoundException(UUID id) {
        super("Pedido con id " + id + " no encontrado.");
    }
}
