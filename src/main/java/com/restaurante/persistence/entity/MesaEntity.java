package com.restaurante.persistence.entity;

import com.restaurante.model.domain.EstadoMesa;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mesas")
@Data
@NoArgsConstructor
public class MesaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer numero;
    private Integer capacidad;
    @Enumerated(EnumType.STRING)
    private EstadoMesa estado;
    private boolean cuentaAbierta;
}