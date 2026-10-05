package com.restaurante.service;

import com.restaurante.mapper.*;
import com.restaurante.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReporteServiceImplTest {
    @Mock private PedidoRepositoryJPA pedidoRepository;
    @Mock private MesaRepositoryJPA mesaRepository;
    @Spy private PedidoEntityMapper pedidoEntityMapper = new PedidoEntityMapper(new ItemPedidoEntityMapper());
    @Spy private MesaEntityMapper mesaEntityMapper = new MesaEntityMapperImpl();
    @InjectMocks private ReporteServiceImpl reporteService;

    @Test
    void calcularIngresosTotales() {
        when(pedidoRepository.findAll()).thenReturn(List.of());
        assertEquals(0.0, reporteService.calcularIngresosTotales());
    }

    @Test
    void obtenerResumenDia() {
        when(pedidoRepository.count()).thenReturn(10L);
        when(mesaRepository.findAll()).thenReturn(List.of(new com.restaurante.persistence.entity.MesaEntity()));
        var r = reporteService.obtenerResumenDia();
        assertEquals(10L, r.get("totalPedidos"));
        assertEquals(0L, r.get("mesasActivas"));
    }

    @Test
    void obtenerPlatosPopulares() {
        when(pedidoRepository.findAll()).thenReturn(List.of());
        assertTrue(reporteService.obtenerPlatosPopulares().isEmpty());
    }
}
