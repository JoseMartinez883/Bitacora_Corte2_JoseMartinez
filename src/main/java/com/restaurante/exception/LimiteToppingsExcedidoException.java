package com.restaurante.exception;

public class LimiteToppingsExcedidoException extends RuntimeException {
    public LimiteToppingsExcedidoException(int cantidad, int maximo) {
        super("Límite de " + maximo + " toppings superado. Recibidos: " + cantidad + ".");
    }
}
