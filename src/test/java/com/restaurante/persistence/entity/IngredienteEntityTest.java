package com.restaurante.persistence.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IngredienteEntityTest {

    @Test
    void restarStock_Suficiente() {
        IngredienteEntity entity = new IngredienteEntity();
        entity.setCantidadDisponible(10);
        entity.setDisponible(true);

        entity.restarStock(5);
        
        assertEquals(5, entity.getCantidadDisponible());
        assertTrue(entity.isDisponible());
    }

    @Test
    void restarStock_Insuficiente() {
        IngredienteEntity entity = new IngredienteEntity();
        entity.setCantidadDisponible(3);
        entity.setDisponible(true);

        entity.restarStock(5);
        
        assertEquals(0, entity.getCantidadDisponible());
        assertFalse(entity.isDisponible());
    }

    @Test
    void restarStock_Exacto() {
        IngredienteEntity entity = new IngredienteEntity();
        entity.setCantidadDisponible(5);
        entity.setDisponible(true);

        entity.restarStock(5);
        
        assertEquals(0, entity.getCantidadDisponible());
        assertFalse(entity.isDisponible());
    }

    @Test
    void gettersAndSetters() {
        IngredienteEntity entity = new IngredienteEntity();
        entity.setId(1L);
        entity.setNombre("Tomate");
        
        assertEquals(1L, entity.getId());
        assertEquals("Tomate", entity.getNombre());
    }
}
