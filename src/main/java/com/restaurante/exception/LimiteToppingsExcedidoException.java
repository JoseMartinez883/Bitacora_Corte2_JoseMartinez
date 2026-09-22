package com.restaurante.exception;

public class LimiteToppingsExcedidoException extends RuntimeException {
    public LimiteToppingsExcedidoException(int cantidad) {
        super("Límite de 5 toppings superado. Recibidos: " + cantidad + ".");
    }
}
