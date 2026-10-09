package com.restaurante.exception;

import java.util.UUID;

public class ResenaDuplicadaException extends RuntimeException {
    public ResenaDuplicadaException(UUID idPedido) {
        super("El pedido " + idPedido + " ya tiene una reseña registrada.");
    }
}
