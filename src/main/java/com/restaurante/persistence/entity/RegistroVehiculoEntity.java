package com.restaurante.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "registro_vehiculos")
@Data
@NoArgsConstructor
public class RegistroVehiculoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String placa;
    private LocalDateTime entrada;
    private LocalDateTime salida;
    private String estado;
    private Double cobro;
}