package com.restaurante.persistence.entity;

import com.restaurante.model.domain.EstadoCuenta;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cuentas")
@Data
@NoArgsConstructor
public class CuentaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idMesa;
    private Double total;
    @Enumerated(EnumType.STRING)
    private EstadoCuenta estado;
    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "cuenta_id")
    private List<ItemPedidoEntity> items = new ArrayList<>();
    private String metodoPago;
}