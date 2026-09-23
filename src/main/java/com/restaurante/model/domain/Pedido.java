package com.restaurante.model.domain;

import com.restaurante.exception.TransicionEstadoInvalidaException;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Pedido {

    private Long id;
    private Long idMesa;
    private List<ItemPedido> items = new ArrayList<>();
    private EstadoPedido estado;
    private LocalDateTime timestamp;

    public boolean puedeModificarse() {
        return estado == EstadoPedido.RECIBIDO;
    }

    public void agregarItem(ItemPedido item) {
        if (!puedeModificarse()) {
            throw new IllegalStateException(
                "No se puede modificar el pedido en estado: " + estado + ". (RN-04)"
            );
        }
        this.items.add(item);
    }

    public void cambiarEstado(EstadoPedido nuevoEstado) {
        if (!esTransicionValida(this.estado, nuevoEstado)) {
            throw new TransicionEstadoInvalidaException(this.estado, nuevoEstado);
        }
        this.estado = nuevoEstado;
    }

    private boolean esTransicionValida(EstadoPedido actual, EstadoPedido destino) {
        return switch (actual) {
            case RECIBIDO       -> destino == EstadoPedido.EN_PREPARACION || destino == EstadoPedido.CANCELADO;
            case EN_PREPARACION -> destino == EstadoPedido.LISTO;
            case LISTO          -> destino == EstadoPedido.ENTREGADO;
            default             -> false;
        };
    }
}
