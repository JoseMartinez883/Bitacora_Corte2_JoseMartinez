package com.restaurante.model.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * El autor de la reseña NO viaja en el request: se toma del token JWT
 * para evitar suplantación.
 */
public record ResenaRequestDTO(
        @NotNull(message = "El id del pedido es obligatorio.") UUID idPedido,
        @NotNull(message = "La calificación es obligatoria.")
        @Min(value = 1, message = "La calificación mínima es 1.")
        @Max(value = 5, message = "La calificación máxima es 5.") Integer calificacion,
        @Size(max = 500, message = "El comentario no puede superar 500 caracteres.") String comentario
) {}
