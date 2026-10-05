package com.restaurante.mapper;

import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.dto.response.CuentaResponseDTO;
import com.restaurante.model.dto.response.ItemPedidoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CuentaMapperOut {

    @Mapping(target = "total", expression = "java(cuenta.calcularTotal())")
    CuentaResponseDTO toResponse(Cuenta cuenta);

    @Mapping(target = "subtotal", expression = "java(item.calcularSubtotal())")
    ItemPedidoResponseDTO toItemResponse(ItemPedido item);
}
