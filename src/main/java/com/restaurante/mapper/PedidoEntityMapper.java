package com.restaurante.mapper;

import com.restaurante.model.domain.Pedido;
import com.restaurante.persistence.entity.PedidoEntity;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PedidoEntityMapper {
    
    private final ItemPedidoEntityMapper itemMapper;

    public PedidoEntity toEntity(Pedido domain) {
        if (domain == null) return null;
        PedidoEntity entity = new PedidoEntity();
        entity.setId(domain.getId());
        entity.setIdMesa(domain.getIdMesa());
        if (domain.getItems() != null) {
            entity.setItems(domain.getItems().stream().map(itemMapper::toEntity).collect(Collectors.toList()));
        }
        entity.setEstado(domain.getEstado());
        entity.setTimestamp(domain.getTimestamp());
        return entity;
    }

    public Pedido toDomain(PedidoEntity entity) {
        if (entity == null) return null;
        Pedido domain = new Pedido();
        domain.setId(entity.getId());
        domain.setIdMesa(entity.getIdMesa());
        if (entity.getItems() != null) {
            domain.setItems(entity.getItems().stream().map(itemMapper::toDomain).collect(Collectors.toList()));
        }
        domain.setEstado(entity.getEstado());
        domain.setTimestamp(entity.getTimestamp());
        return domain;
    }
}
