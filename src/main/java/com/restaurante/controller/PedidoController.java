package com.restaurante.controller;

import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.dto.request.CambioEstadoRequestDTO;
import com.restaurante.model.dto.request.PedidoRequestDTO;
import com.restaurante.model.dto.response.PedidoResponseDTO;
import com.restaurante.service.PedidoService;
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
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Gestión de pedidos y tablero Kanban de cocina")
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'MESERO', 'CLIENTE')")
    @Operation(summary = "Crear pedido", description = "crea el pedido y lo envía a cocina en estado RECIBIDO.")
    @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Mesa o plato no encontrado")
    @ApiResponse(responseCode = "422", description = "Plato agotado o regla de negocio violada")
    public ResponseEntity<PedidoResponseDTO> crearPedido(@Valid @RequestBody PedidoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crearPedido(dto));
    }

    @GetMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'MESERO', 'CHEF')")
    @Operation(summary = "Obtener pedido por ID")
    @ApiResponse(responseCode = "200", description = "Pedido encontrado")
    @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    public ResponseEntity<PedidoResponseDTO> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(pedidoService.obtenerPedidoPorId(id));
    }

    @GetMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'MESERO', 'CHEF')")
    @Operation(summary = "Listar todos los pedidos")
    @ApiResponse(responseCode = "200", description = "Listado de pedidos obtenido exitosamente")
    public ResponseEntity<List<PedidoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @GetMapping("/cocina")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")
    @Operation(summary = "Tablero de cocina por estado", description = "filtra pedidos por estado para el display de cocina.")
    @ApiResponse(responseCode = "200", description = "Tablero de cocina filtrado exitosamente")
    public ResponseEntity<List<PedidoResponseDTO>> tableroCocina(@RequestParam EstadoPedido estado) {
        return ResponseEntity.ok(pedidoService.listarPorEstado(estado));
    }

    @PatchMapping("/{id}/estado")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'MESERO', 'CHEF')")
    @Operation(summary = "Avanzar estado del pedido", description = "transición secuencial estricta de estados. requiere idOperario.")
    @ApiResponse(responseCode = "200", description = "Estado del pedido actualizado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    @ApiResponse(responseCode = "409", description = "Transición de estado inválida")
    public ResponseEntity<PedidoResponseDTO> cambiarEstado(@PathVariable UUID id,
                                                            @Valid @RequestBody CambioEstadoRequestDTO dto) {
        return ResponseEntity.ok(pedidoService.cambiarEstado(id, dto));
    }

    @Operation(summary = "Eliminar pedido")
    @ApiResponse(responseCode = "204", description = "Pedido eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    @DeleteMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> eliminarPedido(@PathVariable UUID id) {
        pedidoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Eliminar ítem de pedido")
    @ApiResponse(responseCode = "200", description = "Ítem eliminado y total recalculado exitosamente")
    @ApiResponse(responseCode = "404", description = "Pedido o ítem no encontrado")
    @DeleteMapping("/{idPedido}/items/{idItem}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'MESERO', 'CLIENTE')")
    public ResponseEntity<PedidoResponseDTO> eliminarItemPedido(@PathVariable UUID idPedido, @PathVariable Long idItem) {
        return ResponseEntity.ok(pedidoService.eliminarItemPedido(idPedido, idItem));
    }

}
