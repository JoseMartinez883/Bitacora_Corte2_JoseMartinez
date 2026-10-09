package com.restaurante.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExceptionsTest {

    @Test
    void testVehiculoNotFoundException() {
        VehiculoNotFoundException ex1 = new VehiculoNotFoundException(1L);
        assertEquals("Registro de vehículo con id 1 no encontrado.", ex1.getMessage());

        VehiculoNotFoundException ex2 = new VehiculoNotFoundException("ABC");
        assertEquals("No se encontró registro activo para la placa: ABC", ex2.getMessage());
    }

    @Test
    void testPlatoNotFoundException() {
        PlatoNotFoundException ex1 = new PlatoNotFoundException(1L);
        assertEquals("Plato con id 1 no encontrado.", ex1.getMessage());
    }
}
