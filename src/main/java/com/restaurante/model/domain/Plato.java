package com.restaurante.model.domain;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class Plato {

    private Long id;
    private String nombre;
    private Double precio;
    private String categoria;
    private boolean disponible;
    private String descripcion;
    private String masa;
    private String salsa;
    private List<String> toppings = new ArrayList<>();
    private List<String> proteinas = new ArrayList<>();
    private List<String> salsasExtras = new ArrayList<>();
    private boolean activo;

    public boolean esValido() {
        return nombre != null && !nombre.isBlank()
                && precio != null && precio > 0
                && categoria != null && !categoria.isBlank()
                && masa != null && !masa.isBlank()
                && salsa != null && !salsa.isBlank();
    }

    public void cambiarDisponibilidad(boolean disponible) {
        this.disponible = disponible;
    }
}
