package com.restaurante.model.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Reserva {

    private Long id;
    private Long idMesa;
    private String cliente;
    private LocalDateTime fechaHora;
    private Integer comensales;
    private boolean activa;

    public boolean estaVigente() {
        return activa && fechaHora != null && fechaHora.isAfter(LocalDateTime.now());
    }

    public void cancelar() {
        this.activa = false;
    }

    public void reprogramar(LocalDateTime nuevaFecha) {
        if (nuevaFecha == null || nuevaFecha.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La nueva fecha debe ser posterior a la fecha actual.");
        }
        this.fechaHora = nuevaFecha;
    }
}
