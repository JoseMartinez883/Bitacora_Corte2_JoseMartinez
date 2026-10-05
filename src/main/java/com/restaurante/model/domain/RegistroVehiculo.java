package com.restaurante.model.domain;

import lombok.Data;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
@Data
public class RegistroVehiculo {

    private Long id;
    private String placa;
    private LocalDateTime entrada;
    private LocalDateTime salida;
    private String estado;
    private Double cobro;

    private static final double TARIFA_POR_HORA = 3000.0;

    public Double calcularCobro() {
        if (entrada == null || salida == null) return 0.0;
        long minutos = Duration.between(entrada, salida).toMinutes();
        long horas = (long) Math.ceil(minutos / 60.0);
        this.cobro = horas * TARIFA_POR_HORA;
        return this.cobro;
    }

    public void registrarSalida() {
        this.salida = LocalDateTime.now(ZoneId.systemDefault());
        this.estado = "FINALIZADO";
        calcularCobro();
    }

    public boolean estaActivo() {
        return salida == null && "ACTIVO".equals(estado);
    }
}
