package com.restaurante.repository;

import com.restaurante.model.domain.Pedido;
import com.restaurante.model.domain.EstadoPedido;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PedidoRepository {

    private final List<Pedido> pedidos = new ArrayList<>();
    private final AtomicLong contador = new AtomicLong(1);

    public Pedido guardar(Pedido pedido) {
        if (pedido.getId() == null) {
            pedido.setId(contador.getAndIncrement());
            pedidos.add(pedido);
        } else {
            pedidos.replaceAll(p -> p.getId().equals(pedido.getId()) ? pedido : p);
        }
        return pedido;
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return pedidos.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    public List<Pedido> buscarTodos() {
        return List.copyOf(pedidos);
    }

    public List<Pedido> buscarPorEstado(EstadoPedido estado) {
        return pedidos.stream()
                .filter(p -> p.getEstado() == estado)
                .toList();
    }
}
