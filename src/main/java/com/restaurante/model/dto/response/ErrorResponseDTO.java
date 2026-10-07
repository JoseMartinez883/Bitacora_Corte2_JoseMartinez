package com.restaurante.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponseDTO(
    int status,
    String error,
    String message,
    String path,
    List<String> details,
    LocalDateTime timestamp
) {
    public ErrorResponseDTO(int status, String error, String message, String path) {
        this(status, error, message, path, null, LocalDateTime.now(java.time.ZoneId.systemDefault()));
    }

    public ErrorResponseDTO(int status, String error, String message, String path, List<String> details) {
        this(status, error, message, path, details, LocalDateTime.now(java.time.ZoneId.systemDefault()));
    }
}
