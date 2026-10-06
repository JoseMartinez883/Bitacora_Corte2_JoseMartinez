package com.restaurante.controller;

import com.restaurante.model.dto.request.PagoCuentaRequestDTO;
import com.restaurante.model.dto.response.CuentaResponseDTO;
import com.restaurante.service.CuentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cuentas")
@RequiredArgsConstructor
@Tag(name = "Cuentas y Pagos", description = "Cierre del ciclo de atención — BCR-05")
public class CuentaController {

    private final CuentaService cuentaService;

    @GetMapping("/mesa/{idMesa}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'MESERO')")
    @Operation(summary = "Ver cuenta activa de una mesa", description = "Calcula el total en tiempo real.")
    public ResponseEntity<CuentaResponseDTO> obtenerCuentaActiva(@PathVariable Long idMesa) {
        return ResponseEntity.ok(cuentaService.obtenerCuentaActivaPorMesa(idMesa));
    }

    @PostMapping("/mesa/{idMesa}/pago")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'MESERO')")
    @Operation(summary = "Registrar pago y cerrar cuenta", description = "cierra la cuenta y libera la mesa.")
    public ResponseEntity<CuentaResponseDTO> registrarPago(@PathVariable Long idMesa,
                                                            @Valid @RequestBody PagoCuentaRequestDTO dto) {
        return ResponseEntity.ok(cuentaService.registrarPago(idMesa, dto));
    }
}
