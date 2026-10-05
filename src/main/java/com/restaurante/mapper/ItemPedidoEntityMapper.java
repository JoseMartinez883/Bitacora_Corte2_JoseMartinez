package com.restaurante.mapper;

import com.restaurante.model.domain.ItemPedido;
import com.restaurante.persistence.entity.ItemPedidoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemPedidoEntityMapper {
    ItemPedidoEntity toEntity(ItemPedido domain);
    ItemPedido toDomain(ItemPedidoEntity entity);
}
