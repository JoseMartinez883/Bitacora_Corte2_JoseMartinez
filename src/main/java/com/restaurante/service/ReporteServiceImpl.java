package com.restaurante.service;
import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.domain.Pedido;
import com.restaurante.repository.PedidoRepositoryJPA;
import com.restaurante.repository.MesaRepositoryJPA;
import com.restaurante.mapper.PedidoEntityMapper;
import com.restaurante.mapper.MesaEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {
    private final PedidoRepositoryJPA pedidoRepository;
    private final MesaRepositoryJPA mesaRepository;
    private final PedidoEntityMapper pedidoEntityMapper;
    private final MesaEntityMapper mesaEntityMapper;

    @Override
    public Map<String, Object> obtenerResumenDia() {
        Map<String, Object> r = new HashMap<>();
        r.put("totalPedidos", pedidoRepository.count());
        r.put("mesasActivas", mesaRepository.findAll().stream().map(mesaEntityMapper::toDomain).filter(Mesa::isCuentaAbierta).count());
        return r;
    }
    @Override
    public Map<String, Long> obtenerPlatosPopulares() {
        return pedidoRepository.findAll().stream().map(pedidoEntityMapper::toDomain).flatMap(p -> p.getItems().stream()).collect(Collectors.groupingBy(ItemPedido::getNombrePlato, Collectors.summingLong(ItemPedido::getCantidad)));
    }
    @Override
    public Double calcularIngresosTotales() {
        return pedidoRepository.findAll().stream().map(pedidoEntityMapper::toDomain).mapToDouble(Pedido::calcularTotal).sum();
    }
}
