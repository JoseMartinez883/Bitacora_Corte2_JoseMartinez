package com.restaurante.service;

import java.util.Map;

public interface ReporteService {
    Map<String, Object> obtenerResumenDia();
    Map<String, Long> obtenerPlatosPopulares();
    Double calcularIngresosTotales();
}

