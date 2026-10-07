package com.restaurante.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reservas")
@Data
@NoArgsConstructor
public class ReservaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Long idMesa;
    private Long usuarioId;
    private String cliente;
    private LocalDateTime fechaHora;
    private Integer comensales;
    private boolean activa;
}