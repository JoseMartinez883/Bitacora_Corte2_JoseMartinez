package com.restaurante.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ingredientes")
@Data
@NoArgsConstructor
public class IngredienteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String nombre;
    
    @Column(nullable = false)
    private Integer cantidadDisponible; // Cantidad en stock (gramos, unidades, etc.)
    
    @Column(nullable = false)
    private boolean disponible;
    
    public void restarStock(Integer cantidad) {
        if (this.cantidadDisponible >= cantidad) {
            this.cantidadDisponible -= cantidad;
        } else {
            this.cantidadDisponible = 0;
        }
        if (this.cantidadDisponible <= 0) {
            this.disponible = false;
        }
    }
}
