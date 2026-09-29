package com.restaurante.mapper;

import com.restaurante.model.domain.ItemPedido;
import com.restaurante.persistence.entity.ItemPedidoEntity;
import org.springframework.stereotype.Component;

@Component
public class ItemPedidoEntityMapper {
    public ItemPedidoEntity toEntity(ItemPedido domain) {
        if (domain == null) return null;
        ItemPedidoEntity entity = new ItemPedidoEntity();
        entity.setId(domain.getId());
        entity.setIdPlato(domain.getIdPlato());
        entity.setNombrePlato(domain.getNombrePlato());
        entity.setPrecioCongelado(domain.getPrecioCongelado());
        entity.setCantidad(domain.getCantidad());
        return entity;
    }

    public ItemPedido toDomain(ItemPedidoEntity entity) {
        if (entity == null) return null;
        ItemPedido domain = new ItemPedido();
        domain.setId(entity.getId());
        domain.setIdPlato(entity.getIdPlato());
        domain.setNombrePlato(entity.getNombrePlato());
        domain.setPrecioCongelado(entity.getPrecioCongelado());
        domain.setCantidad(entity.getCantidad());
        return domain;
    }
}
