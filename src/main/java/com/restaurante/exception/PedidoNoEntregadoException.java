package com.restaurante.exception;

import java.util.UUID;

public class PedidoNoEntregadoException extends RuntimeException {
    public PedidoNoEntregadoException(UUID idPedido) {
        super("Solo se pueden reseñar pedidos ENTREGADOS. El pedido " + idPedido + " aún no ha sido entregado.");
    }
}
