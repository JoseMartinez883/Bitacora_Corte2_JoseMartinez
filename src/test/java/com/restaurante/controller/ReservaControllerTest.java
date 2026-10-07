package com.restaurante.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.model.dto.request.ReservaRequestDTO;
import com.restaurante.model.dto.response.ReservaResponseDTO;
import com.restaurante.service.ReservaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservaController.class)
@org.springframework.context.annotation.Import({com.restaurante.security.SecurityConfig.class, com.restaurante.security.JwtAuthFilter.class})
class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @MockitoBean
    private ReservaService reservaService;

    @MockitoBean
    private com.restaurante.security.JwtUtil jwtUtil;

    @MockitoBean
    private com.restaurante.service.UsuarioDetailsService usuarioDetailsService;
    
    private final UUID TEST_ID = UUID.randomUUID();

    @Test
    void crearReserva_exitoso() throws Exception {
        ReservaRequestDTO req = new ReservaRequestDTO(1L, "Cliente", LocalDateTime.now().plusDays(1), 4);
        ReservaResponseDTO res = new ReservaResponseDTO(TEST_ID, 1L, "Cliente", LocalDateTime.now().plusDays(1), 4, true);
        when(reservaService.crearReserva(any(ReservaRequestDTO.class))).thenReturn(res);

        mockMvc.perform(post("/api/v1/reservas")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("test@cliente.com").roles("CLIENTE"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    void listarTodas_exitoso() throws Exception {
        when(reservaService.listarTodas()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/reservas")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("test@cliente.com").roles("CLIENTE")))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerPorId_exitoso() throws Exception {
        ReservaResponseDTO res = new ReservaResponseDTO(TEST_ID, 1L, "Cliente", LocalDateTime.now().plusDays(1), 4, true);
        when(reservaService.obtenerReservaPorId(TEST_ID)).thenReturn(res);

        mockMvc.perform(get("/api/v1/reservas/" + TEST_ID)
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("test@cliente.com").roles("CLIENTE")))
                .andExpect(status().isOk());
    }

    @Test
    void cancelarReserva_exitoso() throws Exception {
        ReservaResponseDTO res = new ReservaResponseDTO(TEST_ID, 1L, "Cliente", LocalDateTime.now().plusDays(1), 4, false);
        when(reservaService.cancelarReserva(TEST_ID)).thenReturn(res);

        mockMvc.perform(delete("/api/v1/reservas/" + TEST_ID + "/cancelar")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("test@cliente.com").roles("CLIENTE")))
                .andExpect(status().isOk());
    }

    @Test
    void actualizarReserva_exitoso() throws Exception {
        ReservaRequestDTO req = new ReservaRequestDTO(1L, "Cliente", LocalDateTime.now().plusDays(1), 4);
        ReservaResponseDTO res = new ReservaResponseDTO(TEST_ID, 1L, "Cliente", LocalDateTime.now().plusDays(1), 4, true);
        when(reservaService.actualizar(eq(TEST_ID), any(ReservaRequestDTO.class))).thenReturn(res);

        mockMvc.perform(put("/api/v1/reservas/" + TEST_ID)
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("test@cliente.com").roles("CLIENTE"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }
}
