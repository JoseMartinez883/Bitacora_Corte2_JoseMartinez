package com.restaurante.controller;

import com.restaurante.model.dto.request.ResenaRequestDTO;
import com.restaurante.model.dto.response.ResenaResponseDTO;
import com.restaurante.service.ResenaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/resenas")
@RequiredArgsConstructor
@Tag(name = "Reseñas", description = "Reseñas de clientes almacenadas en MongoDB")
public class ResenaController {

    private final ResenaService resenaService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Crear una reseña", description = "Guarda una reseña (1 a 5 estrellas) asociada a un pedido.")
    public ResponseEntity<ResenaResponseDTO> crear(@Valid @RequestBody ResenaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resenaService.crearResena(dto));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar reseñas", description = "Opcionalmente filtra por calificación mínima.")
    public ResponseEntity<List<ResenaResponseDTO>> listar(@RequestParam(required = false) Integer calificacionMinima) {
        if (calificacionMinima != null) {
            return ResponseEntity.ok(resenaService.listarPorCalificacionMinima(calificacionMinima));
        }
        return ResponseEntity.ok(resenaService.listarTodas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener reseña por ID")
    public ResponseEntity<ResenaResponseDTO> obtener(@PathVariable String id) {
        return ResponseEntity.ok(resenaService.obtenerPorId(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Eliminar reseña", description = "Solo el autor de la reseña o un ADMIN pueden eliminarla.")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        resenaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
