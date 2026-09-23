package com.restaurante.controller;

import com.restaurante.model.dto.response.MesaResponseDTO;
import com.restaurante.service.MesaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/mesas")
@RequiredArgsConstructor
@Tag(name = "Mesas", description = "Estado y disponibilidad de las mesas del salón")
public class MesaController {

    private final MesaService mesaService;

    @GetMapping
    @Operation(summary = "Listar todas las mesas")
    public ResponseEntity<List<MesaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(mesaService.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener mesa por ID")
    public ResponseEntity<MesaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mesaService.obtenerMesaPorId(id));
    }

    @PatchMapping("/{id}/abrir-cuenta")
    @Operation(summary = "Abrir cuenta en mesa", description = "una mesa solo puede tener una cuenta activa a la vez.")
    public ResponseEntity<MesaResponseDTO> abrirCuenta(@PathVariable Long id) {
        return ResponseEntity.ok(mesaService.abrirCuenta(id));
    }
}
