package com.restaurante.model.domain;

import com.restaurante.exception.MesaNoDisponibleException;
import lombok.Data;

@Data
public class Mesa {

    private Long id;
    private Integer numero;
    private Integer capacidad;
    private EstadoMesa estado;
    private boolean cuentaAbierta;

    public boolean estaDisponible() {
        return estado == EstadoMesa.DISPONIBLE && !cuentaAbierta;
    }

    public void abrirCuenta() {
        if (!estaDisponible()) {
            throw new MesaNoDisponibleException(this.id);
        }
        this.cuentaAbierta = true;
        this.estado = EstadoMesa.OCUPADA;
    }

    public void cerrarCuenta() {
        this.cuentaAbierta = false;
        this.estado = EstadoMesa.DISPONIBLE;
    }
}
