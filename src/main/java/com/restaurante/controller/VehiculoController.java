package com.restaurante.controller;

import com.restaurante.model.dto.request.RegistroVehiculoRequestDTO;
import com.restaurante.model.dto.response.RegistroVehiculoResponseDTO;
import com.restaurante.service.RegistroVehiculoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping({"/api/v1/vehiculos", "/api/v1/parqueadero"})
@RequiredArgsConstructor
@Tag(name = "Vehículos / Parqueadero", description = "Control de entrada y salida del parqueadero")
@org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
public class VehiculoController {

    private final RegistroVehiculoService vehiculoService;

    @PostMapping("/entrada")
    @Operation(summary = "Registrar entrada de vehículo", description = "Valida cupo máximo (20) y que la placa no esté previamente activa.")
    @ApiResponse(responseCode = "201", description = "Entrada registrada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "409", description = "Placa ya registrada actualmente en el parqueadero")
    @ApiResponse(responseCode = "422", description = "Parqueadero lleno (capacidad máxima alcanzada)")
    public ResponseEntity<RegistroVehiculoResponseDTO> registrarEntrada(@Valid @RequestBody RegistroVehiculoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculoService.registrarEntrada(dto));
    }

    @PatchMapping("/{id}/salida")
    @Operation(summary = "Registrar salida por ID y calcular cobro", description = "Calcula cobro por hora redondeada hacia arriba. $3.000/hora.")
    @ApiResponse(responseCode = "200", description = "Salida registrada exitosamente")
    @ApiResponse(responseCode = "404", description = "Registro de vehículo no encontrado")
    public ResponseEntity<RegistroVehiculoResponseDTO> registrarSalida(@PathVariable Long id) {
        return ResponseEntity.ok(vehiculoService.registrarSalida(id));
    }

    @RequestMapping(value = "/salida/{placa}", method = {RequestMethod.POST, RequestMethod.PATCH})
    @Operation(summary = "Registrar salida por placa y calcular cobro")
    @ApiResponse(responseCode = "200", description = "Salida registrada exitosamente")
    @ApiResponse(responseCode = "404", description = "Placa no encontrada o ya finalizó su estancia")
    public ResponseEntity<RegistroVehiculoResponseDTO> registrarSalidaPorPlaca(@PathVariable String placa) {
        return ResponseEntity.ok(vehiculoService.registrarSalidaPorPlaca(placa));
    }

    @GetMapping("/activos")
    @Operation(summary = "Listar vehículos activos en el parqueadero")
    @ApiResponse(responseCode = "200", description = "Listado de vehículos activos obtenido exitosamente")
    public ResponseEntity<List<RegistroVehiculoResponseDTO>> listarActivos() {
        return ResponseEntity.ok(vehiculoService.listarActivos());
    }

    @GetMapping
    @Operation(summary = "Listar todos los registros")
    @ApiResponse(responseCode = "200", description = "Historial completo de parqueadero obtenido exitosamente")
    public ResponseEntity<List<RegistroVehiculoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(vehiculoService.listarTodos());
    }
}
