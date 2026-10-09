package com.restaurante.controller;

import com.restaurante.model.dto.request.ReservaRequestDTO;
import com.restaurante.model.dto.response.ReservaResponseDTO;
import com.restaurante.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "Gestión de reservas de mesa")
@org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'MESERO', 'CLIENTE')")
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    @Operation(summary = "Crear reserva", description = "Registra una reserva para una mesa validando que no existan traslapes horarios.")
    @ApiResponse(responseCode = "201", description = "Reserva creada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "409", description = "Mesa ya reservada para ese horario")
    public ResponseEntity<ReservaResponseDTO> crearReserva(@Valid @RequestBody ReservaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.crearReserva(dto));
    }

    @GetMapping
    @Operation(summary = "Listar reservas")
    @ApiResponse(responseCode = "200", description = "Listado de reservas obtenido exitosamente")
    public ResponseEntity<List<ReservaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(reservaService.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener reserva por ID")
    @ApiResponse(responseCode = "200", description = "Reserva encontrada")
    @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    public ResponseEntity<ReservaResponseDTO> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(reservaService.obtenerReservaPorId(id));
    }

    @DeleteMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar reserva")
    @ApiResponse(responseCode = "200", description = "Reserva cancelada exitosamente")
    @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    public ResponseEntity<ReservaResponseDTO> cancelarReserva(@PathVariable UUID id) {
        return ResponseEntity.ok(reservaService.cancelarReserva(id));
    }

    @Operation(summary = "Actualizar reserva")
    @ApiResponse(responseCode = "200", description = "Reserva actualizada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    @ApiResponse(responseCode = "409", description = "Conflicto de horario con otra reserva activa")
    @PutMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> actualizarReserva(@PathVariable UUID id, @RequestBody ReservaRequestDTO dto) {
        return ResponseEntity.ok(reservaService.actualizar(id, dto));
    }
}
