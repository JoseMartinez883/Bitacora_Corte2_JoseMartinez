package com.restaurante.mapper;

import com.restaurante.model.domain.Pedido;
import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.dto.response.PedidoResponseDTO;
import com.restaurante.model.dto.response.ItemPedidoResponseDTO;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class PedidoMapperOut {

    public PedidoResponseDTO toResponse(Pedido pedido) {
        List<ItemPedidoResponseDTO> items = pedido.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        return new PedidoResponseDTO(
                pedido.getId(),
                pedido.getIdMesa(),
                pedido.getEstado(),
                items,
                pedido.getTimestamp()
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
