package com.restaurante.service;
import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.domain.Pedido;
import com.restaurante.repository.PedidoRepository;
import com.restaurante.repository.MesaRepository;
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
    private final PedidoRepository pedidoRepository;
    private final MesaRepository mesaRepository;

    @Override
    public Map<String, Object> obtenerResumenDia() {
        log.info("Generando reporte de resumen");
        Map<String, Object> r = new HashMap<>();
        r.put("totalPedidos", pedidoRepository.buscarTodos().size());
        r.put("mesasActivas", mesaRepository.buscarTodas().stream()
                .filter(Mesa::isCuentaAbierta).count());
        return r;
    }
    @Override
    public Map<String, Long> obtenerPlatosPopulares() {
        log.info("Generando platos populares");
        return pedidoRepository.buscarTodos().stream()
                .flatMap(p -> p.getItems().stream())
                .collect(Collectors.groupingBy(ItemPedido::getNombrePlato, Collectors.summingLong(ItemPedido::getCantidad)));
    }
    @Override
    public Double calcularIngresosTotales() {
        log.info("Calculando ingresos");
        return pedidoRepository.buscarTodos().stream().mapToDouble(Pedido::calcularTotal).sum();
    }
}

