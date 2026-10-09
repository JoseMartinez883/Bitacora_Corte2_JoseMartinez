package com.restaurante.exception;

public class VehiculoYaEstacionadoException extends RuntimeException {
    public VehiculoYaEstacionadoException(String placa) {
        super(String.format("El vehículo con placa '%s' ya se encuentra registrado con un ingreso activo en el parqueadero.", placa));
    }
}
