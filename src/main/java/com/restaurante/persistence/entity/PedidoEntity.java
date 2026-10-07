package com.restaurante.persistence.entity;

import com.restaurante.model.domain.EstadoPedido;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
public class PedidoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Long idMesa;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "pedido_id")
    private List<ItemPedidoEntity> items = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;
    private LocalDateTime timestamp;
}