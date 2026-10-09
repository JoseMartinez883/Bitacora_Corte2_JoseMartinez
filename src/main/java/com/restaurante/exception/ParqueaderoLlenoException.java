package com.restaurante.exception;

public class ParqueaderoLlenoException extends RuntimeException {
    public ParqueaderoLlenoException(int capacidadMaxima) {
        super(String.format("El parqueadero está lleno. Se ha alcanzado la capacidad máxima de %d cupos.", capacidadMaxima));
    }
}
