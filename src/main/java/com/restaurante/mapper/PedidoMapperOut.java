package com.restaurante.mapper;

import com.restaurante.model.domain.Pedido;
import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.dto.response.PedidoResponseDTO;
import com.restaurante.model.dto.response.ItemPedidoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PedidoMapperOut {

    PedidoResponseDTO toResponse(Pedido pedido);

    @Mapping(target = "subtotal", expression = "java(item.calcularSubtotal())")
    ItemPedidoResponseDTO toItemResponse(ItemPedido item);
}
