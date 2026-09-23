package com.restaurante.controller;

import com.restaurante.model.dto.request.RegistroVehiculoRequestDTO;
import com.restaurante.model.dto.response.RegistroVehiculoResponseDTO;
import com.restaurante.service.RegistroVehiculoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vehiculos")
@RequiredArgsConstructor
@Tag(name = "Vehículos / Parqueadero", description = "Control de entrada y salida del parqueadero")
public class VehiculoController {

    private final RegistroVehiculoService vehiculoService;

    @PostMapping("/entrada")
    @Operation(summary = "Registrar entrada de vehículo")
    public ResponseEntity<RegistroVehiculoResponseDTO> registrarEntrada(@Valid @RequestBody RegistroVehiculoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculoService.registrarEntrada(dto));
    }

    @PatchMapping("/{id}/salida")
    @Operation(summary = "Registrar salida y calcular cobro", description = "Calcula cobro por hora redondeada hacia arriba. $3.000/hora.")
    public ResponseEntity<RegistroVehiculoResponseDTO> registrarSalida(@PathVariable Long id) {
        return ResponseEntity.ok(vehiculoService.registrarSalida(id));
    }

    @GetMapping("/activos")
    @Operation(summary = "Listar vehículos activos en el parqueadero")
    public ResponseEntity<List<RegistroVehiculoResponseDTO>> listarActivos() {
        return ResponseEntity.ok(vehiculoService.listarActivos());
    }

    @GetMapping
    @Operation(summary = "Listar todos los registros")
    public ResponseEntity<List<RegistroVehiculoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(vehiculoService.listarTodos());
    }
}
