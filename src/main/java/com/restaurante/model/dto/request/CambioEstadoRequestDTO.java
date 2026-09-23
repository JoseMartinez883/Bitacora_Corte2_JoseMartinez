package com.restaurante.model.dto.request;

import com.restaurante.model.domain.EstadoPedido;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CambioEstadoRequestDTO(
        @NotNull(message = "El estado destino es obligatorio.")
        EstadoPedido estadoDestino,

        @NotBlank(message = "El id del operario es obligatorio para auditoría. (RN-05)")
        String idOperario
) {}
