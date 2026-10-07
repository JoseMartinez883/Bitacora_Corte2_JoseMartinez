package com.restaurante.controller;

import com.restaurante.exception.MesaNotFoundException;
import com.restaurante.exception.PlatoAlreadyExistException;
import com.restaurante.model.dto.response.ErrorResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/test");
    }

    @Test
    void handleNotFound_exitoso() {
        MesaNotFoundException ex = new MesaNotFoundException(1L);
        ResponseEntity<ErrorResponseDTO> response = handler.handleNotFound(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Mesa con id 1 no encontrada.", response.getBody().message());
        assertEquals("/api/v1/test", response.getBody().path());
    }

    @Test
    void handleConflict_exitoso() {
        PlatoAlreadyExistException ex = new PlatoAlreadyExistException("Pizza");
        ResponseEntity<ErrorResponseDTO> response = handler.handleConflict(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Ya existe un plato con el nombre: Pizza", response.getBody().message());
        assertEquals("/api/v1/test", response.getBody().path());
    }

    @Test
    void handleBusinessRule_exitoso() {
        IllegalArgumentException ex = new IllegalArgumentException("Supera limite");
        ResponseEntity<ErrorResponseDTO> response = handler.handleBusinessRule(ex, request);

        assertEquals(HttpStatus.valueOf(422), response.getStatusCode());
        assertEquals("Supera limite", response.getBody().message());
        assertEquals("/api/v1/test", response.getBody().path());
    }

    @Test
    void handleValidationExceptions_exitoso() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        FieldError error1 = new FieldError("objectName", "field1", "Error message 1");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(error1));

        ResponseEntity<ErrorResponseDTO> response = handler.handleValidation(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation Failed", response.getBody().message());
        List<String> messages = response.getBody().details();
        assertEquals("field1: Error message 1", messages.get(0));
    }
}
