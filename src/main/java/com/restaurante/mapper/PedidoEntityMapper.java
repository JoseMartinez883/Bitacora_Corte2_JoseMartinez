package com.restaurante.mapper;

import com.restaurante.model.domain.Pedido;
import com.restaurante.persistence.entity.PedidoEntity;
import org.mapstruct.Mapper;

import org.mapstruct.InjectionStrategy;
@Mapper(componentModel = "spring", uses = {ItemPedidoEntityMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PedidoEntityMapper {
    PedidoEntity toEntity(Pedido domain);
    Pedido toDomain(PedidoEntity entity);
}
