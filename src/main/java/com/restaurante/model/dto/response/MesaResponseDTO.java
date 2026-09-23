package com.restaurante.model.dto.response;

import com.restaurante.model.domain.EstadoMesa;

public record MesaResponseDTO(
        Long id,
        Integer numero,
        Integer capacidad,
        EstadoMesa estado,
        boolean cuentaAbierta
) {}
