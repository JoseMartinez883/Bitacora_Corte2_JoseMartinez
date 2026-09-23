package com.restaurante.mapper;

import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.dto.response.CuentaResponseDTO;
import com.restaurante.model.dto.response.ItemPedidoResponseDTO;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class CuentaMapperOut {

    public CuentaResponseDTO toResponse(Cuenta cuenta) {
        List<ItemPedidoResponseDTO> items = cuenta.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        return new CuentaResponseDTO(
                cuenta.getId(),
                cuenta.getIdMesa(),
                cuenta.getEstado(),
                items,
                cuenta.calcularTotal(),
                cuenta.getFechaApertura(),
                cuenta.getFechaCierre(),
                cuenta.getMetodoPago()
        );
    }

    private ItemPedidoResponseDTO toItemResponse(ItemPedido item) {
        return new ItemPedidoResponseDTO(
                item.getId(),
                item.getIdPlato(),
                item.getNombrePlato(),
                item.getPrecioCongelado(),
                item.getCantidad(),
                item.calcularSubtotal()
        );
    }
}
