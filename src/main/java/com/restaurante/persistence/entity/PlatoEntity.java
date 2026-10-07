package com.restaurante.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "platos")
@Data
@NoArgsConstructor
public class PlatoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private Double precio;
    private String categoria;
    private boolean disponible;
    private String descripcion;
    private String masa;
    private String salsa;
    @ElementCollection
    private List<String> toppings = new ArrayList<>();
    @ElementCollection
    private List<String> proteinas = new ArrayList<>();
    @ElementCollection
    private List<String> salsasExtras = new ArrayList<>();
    private boolean activo;
}