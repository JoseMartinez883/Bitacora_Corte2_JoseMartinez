package com.restaurante.controller;

import com.restaurante.model.dto.request.PlatoRequestDTO;
import com.restaurante.model.dto.response.PlatoResponseDTO;
import com.restaurante.service.PlatoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/platos")
@RequiredArgsConstructor
@Tag(name = "Platos", description = "Administración del menú — vista del Administrador")
public class PlatoController {

    private final PlatoService platoService;

    @PostMapping
    @Operation(summary = "Crear un plato", description = "Crea un nuevo plato. masa y salsa obligatorias. máx 5 toppings.")
    public ResponseEntity<PlatoResponseDTO> crearPlato(@Valid @RequestBody PlatoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(platoService.crearPlato(dto));
    }

    @GetMapping
    @Operation(summary = "Listar todos los platos", description = "Retorna todos los platos incluyendo inactivos (vista admin).")
    public ResponseEntity<List<PlatoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(platoService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener plato por ID")
    public ResponseEntity<PlatoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(platoService.obtenerPlatoPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un plato")
    public ResponseEntity<PlatoResponseDTO> actualizarPlato(@PathVariable Long id,
                                                             @Valid @RequestBody PlatoRequestDTO dto) {
        return ResponseEntity.ok(platoService.actualizarPlato(id, dto));
    }

    @PatchMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar un plato", description = "desactiva sin borrar historial.")
    public ResponseEntity<PlatoResponseDTO> desactivarPlato(@PathVariable Long id) {
        return ResponseEntity.ok(platoService.desactivarPlato(id));
    }

    @PatchMapping("/{id}/agotado")
    @Operation(summary = "Marcar plato como agotado", description = "marca como no disponible por falta de ingredientes.")
    public ResponseEntity<PlatoResponseDTO> marcarAgotado(@PathVariable Long id) {
        return ResponseEntity.ok(platoService.marcarAgotado(id));
    }
}
