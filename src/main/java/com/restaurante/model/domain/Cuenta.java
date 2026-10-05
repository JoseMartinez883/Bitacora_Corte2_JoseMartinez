package com.restaurante.model.domain;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Cuenta {

    private Long id;
    private Long idMesa;
    private Double total;
    private EstadoCuenta estado;
    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre;
    private List<ItemPedido> items = new ArrayList<>();
    private String metodoPago;

    public Double calcularTotal() {
        this.total = items.stream()
                .mapToDouble(ItemPedido::calcularSubtotal)
                .sum();
        return this.total;
    }

    public void agregarCargo(ItemPedido item) {
        this.items.add(item);
    }

    public void cerrarCuenta(String metodoPago) {
        this.metodoPago = metodoPago;
        this.estado = EstadoCuenta.CERRADA;
        this.fechaCierre = LocalDateTime.now(java.time.ZoneId.systemDefault());
        calcularTotal();
    }
}
