package com.restaurante.model.dto.response;

import java.time.LocalDateTime;

public record RegistroVehiculoResponseDTO(
        Long id,
        String placa,
        LocalDateTime entrada,
        LocalDateTime salida,
        String estado,
        Double cobro
) {}
