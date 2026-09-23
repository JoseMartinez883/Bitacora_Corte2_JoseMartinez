package com.restaurante.model.dto.response;

import com.restaurante.model.domain.EstadoCuenta;
import java.time.LocalDateTime;
import java.util.List;

public record CuentaResponseDTO(
        Long id,
        Long idMesa,
        EstadoCuenta estado,
        List<ItemPedidoResponseDTO> items,
        Double total,
        LocalDateTime fechaApertura,
        LocalDateTime fechaCierre,
        String metodoPago
) {}
