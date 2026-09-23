package com.restaurante.model.domain;

import lombok.Data;

@Data
public class ItemPedido {

    private Long id;
    private Long idPlato;
    private String nombrePlato;
    private Double precioCongelado;
    private Integer cantidad;

    public Double calcularSubtotal() {
        if (precioCongelado == null || cantidad == null) return 0.0;
        return precioCongelado * cantidad;
    }

    public void actualizarCantidad(int nuevaCantidad) {
        if (nuevaCantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
        }
        this.cantidad = nuevaCantidad;
    }

    public boolean esValido() {
        return idPlato != null && idPlato > 0
                && cantidad != null && cantidad > 0
                && precioCongelado != null && precioCongelado > 0;
    }
}
