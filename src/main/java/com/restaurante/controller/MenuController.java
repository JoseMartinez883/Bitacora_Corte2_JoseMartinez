package com.restaurante.controller;

import com.restaurante.model.dto.response.PlatoResponseDTO;
import com.restaurante.service.PlatoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/menu")
@RequiredArgsConstructor
@Tag(name = "Menú", description = "Vista del cliente — solo platos disponibles (RF1)")
public class MenuController {

    private final PlatoService platoService;

    @GetMapping
    @Operation(summary = "Ver el menú del restaurante", description = "RF1: retorna únicamente los platos disponibles y activos.")
    @ApiResponse(responseCode = "200", description = "Menú disponible obtenido exitosamente")
    public ResponseEntity<List<PlatoResponseDTO>> verMenu() {
        return ResponseEntity.ok(platoService.listarDisponibles());
    }
}
