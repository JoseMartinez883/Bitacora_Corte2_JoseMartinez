package com.restaurante.controller;

import com.restaurante.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes")
@org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
public class ReporteController {
    private final ReporteService reporteService;

    @Operation(summary = "Resumen del día")
    @GetMapping("/resumen")

    public ResponseEntity<Map<String, Object>> getResumen() { return ResponseEntity.ok(reporteService.obtenerResumenDia()); }

    @Operation(summary = "Platos más vendidos")
    @GetMapping("/platos-populares")
    public ResponseEntity<Map<String, Long>> getPlatosPopulares() { return ResponseEntity.ok(reporteService.obtenerPlatosPopulares()); }

    @Operation(summary = "Ingresos totales")
    @GetMapping("/ingresos")
    public ResponseEntity<Double> getIngresos() { return ResponseEntity.ok(reporteService.calcularIngresosTotales()); }
}

