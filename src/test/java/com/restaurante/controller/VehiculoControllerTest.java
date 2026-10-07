package com.restaurante.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.model.dto.request.RegistroVehiculoRequestDTO;
import com.restaurante.model.dto.response.RegistroVehiculoResponseDTO;
import com.restaurante.service.RegistroVehiculoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VehiculoController.class)
@org.springframework.context.annotation.Import({com.restaurante.security.SecurityConfig.class, com.restaurante.security.JwtAuthFilter.class})
class VehiculoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @MockitoBean
    private RegistroVehiculoService vehiculoService;

    @MockitoBean
    private com.restaurante.security.JwtUtil jwtUtil;

    @MockitoBean
    private com.restaurante.service.UsuarioDetailsService usuarioDetailsService;

    @Test
    void registrarEntrada_exitoso() throws Exception {
        RegistroVehiculoRequestDTO req = new RegistroVehiculoRequestDTO("ABC-123");
        RegistroVehiculoResponseDTO res = new RegistroVehiculoResponseDTO(1L, "ABC-123", LocalDateTime.now(), null, "ACTIVO", 0.0);
        when(vehiculoService.registrarEntrada(any(RegistroVehiculoRequestDTO.class))).thenReturn(res);

        mockMvc.perform(post("/api/v1/vehiculos/entrada")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    void registrarSalida_exitoso() throws Exception {
        RegistroVehiculoResponseDTO res = new RegistroVehiculoResponseDTO(1L, "ABC-123", LocalDateTime.now(), LocalDateTime.now().plusHours(1), "INACTIVO", 5000.0);
        when(vehiculoService.registrarSalida(1L)).thenReturn(res);

        mockMvc.perform(patch("/api/v1/vehiculos/1/salida")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void listarActivos_exitoso() throws Exception {
        when(vehiculoService.listarActivos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/vehiculos/activos")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }
}
