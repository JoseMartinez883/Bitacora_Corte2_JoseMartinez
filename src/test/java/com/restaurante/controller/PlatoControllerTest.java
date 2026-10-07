package com.restaurante.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.model.dto.request.PlatoRequestDTO;
import com.restaurante.model.dto.response.PlatoResponseDTO;
import com.restaurante.service.PlatoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlatoController.class)
@org.springframework.context.annotation.Import({com.restaurante.security.SecurityConfig.class, com.restaurante.security.JwtAuthFilter.class})
class PlatoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @MockitoBean
    private PlatoService platoService;

    @MockitoBean
    private com.restaurante.security.JwtUtil jwtUtil;

    @MockitoBean
    private com.restaurante.service.UsuarioDetailsService usuarioDetailsService;

    @Test
    void crearPlato_exitoso() throws Exception {
        PlatoRequestDTO req = new PlatoRequestDTO("Pizza", 10.0, "Principal", "Masa fina", "Salsa roja", List.of(), List.of(), List.of(), "Rica pizza");
        PlatoResponseDTO res = new PlatoResponseDTO(1L, "Pizza", 10.0, "Principal", "Masa fina", "Salsa roja", List.of(), List.of(), List.of(), "Rica pizza", true, true);

        when(platoService.crearPlato(any(PlatoRequestDTO.class))).thenReturn(res);

        mockMvc.perform(post("/api/v1/platos")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Pizza"));
    }

    @Test
    void listarTodos_exitoso() throws Exception {
        when(platoService.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/platos")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void obtenerPorId_exitoso() throws Exception {
        PlatoResponseDTO res = new PlatoResponseDTO(1L, "Pizza", 10.0, "Principal", "Masa fina", "Salsa roja", List.of(), List.of(), List.of(), "Rica pizza", true, true);
        when(platoService.obtenerPlatoPorId(1L)).thenReturn(res);

        mockMvc.perform(get("/api/v1/platos/1")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void actualizarPlato_exitoso() throws Exception {
        PlatoRequestDTO req = new PlatoRequestDTO("Pizza", 10.0, "Principal", "Masa fina", "Salsa roja", List.of(), List.of(), List.of(), "Rica pizza");
        PlatoResponseDTO res = new PlatoResponseDTO(1L, "Pizza", 10.0, "Principal", "Masa fina", "Salsa roja", List.of(), List.of(), List.of(), "Rica pizza", true, true);
        
        when(platoService.actualizarPlato(eq(1L), any(PlatoRequestDTO.class))).thenReturn(res);

        mockMvc.perform(put("/api/v1/platos/1")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void desactivarPlato_exitoso() throws Exception {
        PlatoResponseDTO res = new PlatoResponseDTO(1L, "Pizza", 10.0, "Principal", "Masa fina", "Salsa roja", List.of(), List.of(), List.of(), "Rica pizza", false, true);
        when(platoService.desactivarPlato(1L)).thenReturn(res);

        mockMvc.perform(patch("/api/v1/platos/1/desactivar")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void marcarAgotado_exitoso() throws Exception {
        PlatoResponseDTO res = new PlatoResponseDTO(1L, "Pizza", 10.0, "Principal", "Masa fina", "Salsa roja", List.of(), List.of(), List.of(), "Rica pizza", true, false);
        when(platoService.marcarAgotado(1L)).thenReturn(res);

        mockMvc.perform(patch("/api/v1/platos/1/agotado")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("CHEF")))
                .andExpect(status().isOk());
    }

    @Test
    void eliminarPlato_exitoso() throws Exception {
        mockMvc.perform(delete("/api/v1/platos/1")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());
        verify(platoService).eliminar(1L);
    }
}
