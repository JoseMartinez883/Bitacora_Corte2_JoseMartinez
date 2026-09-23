package com.restaurante.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatoEntity {

    private Long id;
    private String nombre;
    private Double precio;
    private String categoria;
    private boolean disponible;
    private String descripcion;
    private String masa;
    private String salsa;

    @Builder.Default
    private List<String> toppings = new ArrayList<>();

    private boolean activo;
}
