package com.restaurante.controller;

import com.restaurante.model.dto.request.CatalogoRequestDTO;
import com.restaurante.model.dto.response.CatalogoResponseDTO;
import com.restaurante.service.CatalogoService;
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
@RequestMapping("/api/v1/catalogos")
@RequiredArgsConstructor
@Tag(name = "Catálogo de fotos", description = "Catálogo de imágenes de platos almacenado en MongoDB")
public class CatalogoController {

    private final CatalogoService catalogoService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")
    @Operation(summary = "Crear catálogo de fotos de un plato")
    public ResponseEntity<CatalogoResponseDTO> crear(@Valid @RequestBody CatalogoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogoService.crearCatalogo(dto));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar catálogos")
    public ResponseEntity<List<CatalogoResponseDTO>> listar() {
        return ResponseEntity.ok(catalogoService.listarTodos());
    }

    @GetMapping("/plato/{idPlato}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener catálogo por ID de plato")
    public ResponseEntity<CatalogoResponseDTO> obtenerPorPlato(@PathVariable Long idPlato) {
        return ResponseEntity.ok(catalogoService.obtenerPorIdPlato(idPlato));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")
    @Operation(summary = "Actualizar catálogo")
    public ResponseEntity<CatalogoResponseDTO> actualizar(@PathVariable String id,
                                                          @Valid @RequestBody CatalogoRequestDTO dto) {
        return ResponseEntity.ok(catalogoService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar catálogo")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        catalogoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
