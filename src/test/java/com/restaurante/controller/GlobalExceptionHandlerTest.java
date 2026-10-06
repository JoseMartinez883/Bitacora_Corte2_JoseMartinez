package com.restaurante.controller;

import com.restaurante.exception.MesaNotFoundException;
import com.restaurante.exception.PlatoAlreadyExistException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_exitoso() {
        MesaNotFoundException ex = new MesaNotFoundException(1L);
        ResponseEntity<Map<String, Object>> response = handler.handleNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Mesa con id 1 no encontrada.", response.getBody().get("message"));
    }

    @Test
    void handleConflict_exitoso() {
        PlatoAlreadyExistException ex = new PlatoAlreadyExistException("Pizza");
        ResponseEntity<Map<String, Object>> response = handler.handleConflict(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Ya existe un plato con el nombre: Pizza", response.getBody().get("message"));
    }

    @Test
    void handleBusinessRule_exitoso() {
        IllegalArgumentException ex = new IllegalArgumentException("Supera limite");
        ResponseEntity<Map<String, Object>> response = handler.handleBusinessRule(ex);

        assertEquals(HttpStatus.valueOf(422), response.getStatusCode());
        assertEquals("Supera limite", response.getBody().get("message"));
    }

    @Test
    void handleValidationExceptions_exitoso() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        FieldError error1 = new FieldError("objectName", "field1", "Error message 1");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(error1));

        ResponseEntity<Map<String, Object>> response = handler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        @SuppressWarnings("unchecked")
        List<String> messages = (List<String>) response.getBody().get("messages");
        assertEquals("field1: Error message 1", messages.get(0));
    }
}
