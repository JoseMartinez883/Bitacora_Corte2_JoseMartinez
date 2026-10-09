package com.restaurante.controller;

import com.restaurante.exception.*;
import com.restaurante.model.dto.response.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---- 404 Not Found ----
    @ExceptionHandler({PlatoNotFoundException.class, PedidoNotFoundException.class,
                        MesaNotFoundException.class, CuentaNotFoundException.class,
                        ReservaNotFoundException.class, VehiculoNotFoundException.class,
                        ResenaNotFoundException.class, CatalogoNotFoundException.class})
    public ResponseEntity<ErrorResponseDTO> handleNotFound(RuntimeException ex, HttpServletRequest request) {
        log.warn("Recurso no encontrado [404] en {}: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI(), null);
    }

    // ---- 401 Unauthorized (credenciales invalidas en login) ----
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> handleAuthentication(org.springframework.security.core.AuthenticationException ex, HttpServletRequest request) {
        log.warn("Intento de login fallido [401] en {} desde {}", request.getRequestURI(), request.getRemoteAddr());
        return buildResponse(HttpStatus.UNAUTHORIZED, "Credenciales invalidas", request.getRequestURI(), null);
    }

    // ---- 403 Forbidden ----
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDenied(org.springframework.security.access.AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Acceso denegado [403] en {}: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, "Acceso denegado: No tienes el rol necesario para esta accion.", request.getRequestURI(), null);
    }

    // ---- 409 Conflict ----
    @ExceptionHandler({PlatoAlreadyExistException.class, MesaNoDisponibleException.class,
                        TransicionEstadoInvalidaException.class, ResenaDuplicadaException.class,
                        VehiculoYaEstacionadoException.class, MesaYaReservadaException.class})
    public ResponseEntity<ErrorResponseDTO> handleConflict(RuntimeException ex, HttpServletRequest request) {
        log.warn("Conflicto [409] en {}: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI(), null);
    }

    // ---- 422 Unprocessable Entity (Reglas de negocio) ----
    @ExceptionHandler({LimiteToppingsExcedidoException.class, PlatoNoDisponibleException.class,
                        IllegalArgumentException.class, IllegalStateException.class,
                        PedidoNoEntregadoException.class, ParqueaderoLlenoException.class,
                        PedidoEnCocinaException.class})
    public ResponseEntity<ErrorResponseDTO> handleBusinessRule(RuntimeException ex, HttpServletRequest request) {
        log.warn("Regla de negocio violada [422] en {}: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.valueOf(422), ex.getMessage(), request.getRequestURI(), null);
    }

    // ---- 400 Bad Request (@Valid & formato JSON/UUID inválido) ----
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList();
        log.warn("Fallo de validación [400] en {}: {}", request.getRequestURI(), errors);
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation Failed", request.getRequestURI(), errors);
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotReadable(org.springframework.http.converter.HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("Error en el formato del request body [400] en {}: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "El cuerpo de la petición tiene un formato inválido (por ejemplo, idPedido debe ser un UUID válido).", request.getRequestURI(), null);
    }

    // ---- 500 Internal Server Error (Errores no controlados) ----
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Error inesperado del servidor [500] en {}: ", request.getRequestURI(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error interno en el servidor", request.getRequestURI(), null);
    }

    private ResponseEntity<ErrorResponseDTO> buildResponse(HttpStatus status, String message, String path, List<String> details) {
        ErrorResponseDTO errorResponse = (details == null || details.isEmpty()) 
            ? new ErrorResponseDTO(status.value(), status.getReasonPhrase(), message, path)
            : new ErrorResponseDTO(status.value(), status.getReasonPhrase(), message, path, details);
        return ResponseEntity.status(status).body(errorResponse);
    }
}
