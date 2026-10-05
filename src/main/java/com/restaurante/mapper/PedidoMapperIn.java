package com.restaurante.mapper;

import com.restaurante.model.domain.Pedido;
import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.dto.request.PedidoRequestDTO;
import com.restaurante.model.dto.request.ItemPedidoRequestDTO;
import com.restaurante.model.domain.Plato;
import org.mapstruct.Mapper;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PedidoMapperIn {

    default Pedido toDomain(PedidoRequestDTO dto, List<Plato> platos) {
        Pedido pedido = new Pedido();
        pedido.setIdMesa(dto.idMesa());
        pedido.setEstado(EstadoPedido.RECIBIDO);
        pedido.setTimestamp(LocalDateTime.now(java.time.ZoneId.systemDefault()));

        List<ItemPedido> items = dto.items().stream()
                .map(itemDto -> toItemDomain(itemDto, platos))
                .toList();
        pedido.setItems(items);
        return pedido;
    }

    private ItemPedido toItemDomain(ItemPedidoRequestDTO dto, List<Plato> platos) {
        Plato plato = platos.stream()
                .filter(p -> p.getId().equals(dto.idPlato()))
                .findFirst()
                .orElseThrow();

        ItemPedido item = new ItemPedido();
        item.setIdPlato(plato.getId());
        item.setNombrePlato(plato.getNombre());
        item.setPrecioCongelado(plato.getPrecio()); 
        item.setCantidad(dto.cantidad());
        return item;
    }
}
