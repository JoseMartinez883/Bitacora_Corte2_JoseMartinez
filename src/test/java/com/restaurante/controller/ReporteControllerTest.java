package com.restaurante.controller;

import com.restaurante.service.ReporteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReporteController.class)
class ReporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReporteService reporteService;

    @Test
    void resumenDiario_exitoso() throws Exception {
        when(reporteService.obtenerResumenDia()).thenReturn(Map.of());

        mockMvc.perform(get("/api/v1/reportes/resumen"))
                .andExpect(status().isOk());
    }

    @Test
    void platosMasPopulares_exitoso() throws Exception {
        when(reporteService.obtenerPlatosPopulares()).thenReturn(Map.of());

        mockMvc.perform(get("/api/v1/reportes/platos-populares"))
                .andExpect(status().isOk());
    }

    @Test
    void ingresosTotales_exitoso() throws Exception {
        when(reporteService.calcularIngresosTotales()).thenReturn(100.0);

        mockMvc.perform(get("/api/v1/reportes/ingresos"))
                .andExpect(status().isOk());
    }
}
