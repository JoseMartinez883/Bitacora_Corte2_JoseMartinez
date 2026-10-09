package com.restaurante.exception;

import com.restaurante.model.domain.EstadoPedido;

public class PedidoEnCocinaException extends RuntimeException {
    public PedidoEnCocinaException(EstadoPedido estadoActual) {
        super(String.format("No se puede eliminar o cancelar el pedido porque ya pasó a cocina o fue entregado (estado actual: %s). Solo se permiten cancelaciones en estado RECIBIDO.", estadoActual));
    }
}
