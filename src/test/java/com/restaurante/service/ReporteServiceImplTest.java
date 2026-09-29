package com.restaurante.service;

import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.domain.Pedido;
import com.restaurante.repository.MesaRepository;
import com.restaurante.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReporteServiceImplTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private MesaRepository mesaRepository;

    @InjectMocks
    private ReporteServiceImpl reporteService;

    private Pedido pedido1;
    private Pedido pedido2;
    private Mesa mesa1;
    private Mesa mesa2;

    @BeforeEach
    void setUp() {
        ItemPedido item1 = new ItemPedido();
        item1.setNombrePlato("Pizza");
        item1.setCantidad(2);
        item1.setPrecioCongelado(20000.0);

        ItemPedido item2 = new ItemPedido();
        item2.setNombrePlato("Pasta");
        item2.setCantidad(1);
        item2.setPrecioCongelado(10000.0);

        ItemPedido item3 = new ItemPedido();
        item3.setNombrePlato("Pizza");
        item3.setCantidad(3);
        item3.setPrecioCongelado(10000.0);

        pedido1 = new Pedido();
        pedido1.setItems(Arrays.asList(item1, item2));
        // Total = (2 * 20000) + (1 * 10000) = 50000.0

        pedido2 = new Pedido();
        pedido2.setItems(Collections.singletonList(item3));
        // Total = (3 * 10000) = 30000.0

        mesa1 = new Mesa();
        mesa1.setCuentaAbierta(true);

        mesa2 = new Mesa();
        mesa2.setCuentaAbierta(false);
    }

    @Test
    @DisplayName("1. Happy Path: obtenerResumenDia cuenta pedidos y mesas activas")
    void obtenerResumenDia_RetornaResumenCorrecto() {
        when(pedidoRepository.buscarTodos()).thenReturn(Arrays.asList(pedido1, pedido2));
        when(mesaRepository.buscarTodas()).thenReturn(Arrays.asList(mesa1, mesa2));

        Map<String, Object> resumen = reporteService.obtenerResumenDia();

        assertEquals(2, resumen.get("totalPedidos"));
        assertEquals(1L, resumen.get("mesasActivas"));
    }

    @Test
    @DisplayName("2. Happy Path: obtenerPlatosPopulares agrupa cantidades correctamente")
    void obtenerPlatosPopulares_RetornaCantidadesAgrupadas() {
        when(pedidoRepository.buscarTodos()).thenReturn(Arrays.asList(pedido1, pedido2));

        Map<String, Long> populares = reporteService.obtenerPlatosPopulares();

        assertEquals(5L, populares.get("Pizza"));
        assertEquals(1L, populares.get("Pasta"));
    }

    @Test
    @DisplayName("3. Happy Path: calcularIngresosTotales suma los totales de los pedidos")
    void calcularIngresosTotales_RetornaSumaDeTotales() {
        when(pedidoRepository.buscarTodos()).thenReturn(Arrays.asList(pedido1, pedido2));

        Double ingresos = reporteService.calcularIngresosTotales();

        assertEquals(80000.0, ingresos);
    }
}
