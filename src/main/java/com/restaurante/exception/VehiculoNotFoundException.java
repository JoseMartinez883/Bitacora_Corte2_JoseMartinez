package com.restaurante.exception;

public class VehiculoNotFoundException extends RuntimeException {
    public VehiculoNotFoundException(Long id) {
        super("Registro de vehículo con id " + id + " no encontrado.");
    }
    public VehiculoNotFoundException(String placa) {
        super("No se encontró registro activo para la placa: " + placa);
    }
}
