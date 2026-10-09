package com.restaurante.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.exception.PedidoNoEntregadoException;
import com.restaurante.exception.ResenaDuplicadaException;
import com.restaurante.exception.ResenaNotFoundException;
import com.restaurante.model.dto.request.ResenaRequestDTO;
import com.restaurante.model.dto.response.ResenaResponseDTO;
import com.restaurante.service.ResenaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResenaController.class)
@org.springframework.context.annotation.Import({com.restaurante.security.SecurityConfig.class, com.restaurante.security.JwtAuthFilter.class})
class ResenaControllerTest {

    @Autowired private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @MockitoBean private ResenaService resenaService;
    @MockitoBean private com.restaurante.security.JwtUtil jwtUtil;
    @MockitoBean private com.restaurante.security.CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    @MockitoBean private com.restaurante.service.UsuarioDetailsService usuarioDetailsService;

    private final UUID idPedido = UUID.randomUUID();

    private ResenaResponseDTO res() {
        return new ResenaResponseDTO("r1", idPedido, "ana", 5, "Excelente", LocalDateTime.now());
    }

    private String body(int calificacion) throws Exception {
        return objectMapper.writeValueAsString(new ResenaRequestDTO(idPedido, calificacion, "Excelente"));
    }

    @Test
    void crear_exitoso() throws Exception {
        when(resenaService.crearResena(any(ResenaRequestDTO.class))).thenReturn(res());
        mockMvc.perform(post("/api/v1/resenas")
                        .with(user("ana@test.com").roles("CLIENTE"))
                        .contentType(MediaType.APPLICATION_JSON).content(body(5)))
                .andExpect(status().isCreated());
    }

    @Test
    void crear_calificacionInvalida_retorna400() throws Exception {
        mockMvc.perform(post("/api/v1/resenas")
                        .with(user("ana@test.com").roles("CLIENTE"))
                        .contentType(MediaType.APPLICATION_JSON).content(body(9)))
                .andExpect(status().isBadRequest());
        verify(resenaService, never()).crearResena(any());
    }

    @Test
    void crear_pedidoNoEntregado_retorna422() throws Exception {
        when(resenaService.crearResena(any())).thenThrow(new PedidoNoEntregadoException(idPedido));
        mockMvc.perform(post("/api/v1/resenas")
                        .with(user("ana@test.com").roles("CLIENTE"))
                        .contentType(MediaType.APPLICATION_JSON).content(body(5)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void crear_pedidoYaResenado_retorna409() throws Exception {
        when(resenaService.crearResena(any())).thenThrow(new ResenaDuplicadaException(idPedido));
        mockMvc.perform(post("/api/v1/resenas")
                        .with(user("ana@test.com").roles("CLIENTE"))
                        .contentType(MediaType.APPLICATION_JSON).content(body(5)))
                .andExpect(status().isConflict());
    }

    @Test
    void crear_sinAutenticacion_retorna401() throws Exception {
        mockMvc.perform(post("/api/v1/resenas")
                        .contentType(MediaType.APPLICATION_JSON).content(body(5)))
                .andExpect(status().isUnauthorized());
        verify(resenaService, never()).crearResena(any());
    }

    @Test
    void listar_exitoso() throws Exception {
        when(resenaService.listarTodas()).thenReturn(List.of(res()));
        mockMvc.perform(get("/api/v1/resenas").with(user("ana@test.com").roles("CLIENTE")))
                .andExpect(status().isOk());
    }

    @Test
    void listar_conCalificacionMinima() throws Exception {
        when(resenaService.listarPorCalificacionMinima(4)).thenReturn(List.of(res()));
        mockMvc.perform(get("/api/v1/resenas").param("calificacionMinima", "4")
                        .with(user("ana@test.com").roles("CLIENTE")))
                .andExpect(status().isOk());
        verify(resenaService).listarPorCalificacionMinima(4);
    }

    @Test
    void obtener_exitoso() throws Exception {
        when(resenaService.obtenerPorId("r1")).thenReturn(res());
        mockMvc.perform(get("/api/v1/resenas/r1").with(user("ana@test.com").roles("CLIENTE")))
                .andExpect(status().isOk());
    }

    @Test
    void obtener_noExiste_retorna404() throws Exception {
        when(resenaService.obtenerPorId("x")).thenThrow(new ResenaNotFoundException("x"));
        mockMvc.perform(get("/api/v1/resenas/x").with(user("ana@test.com").roles("CLIENTE")))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminar_autorOAdmin_retorna204() throws Exception {
        mockMvc.perform(delete("/api/v1/resenas/r1").with(user("ana@test.com").roles("CLIENTE")))
                .andExpect(status().isNoContent());
        verify(resenaService).eliminar("r1");
    }

    @Test
    void eliminar_resenaAjena_retorna403() throws Exception {
        doThrow(new AccessDeniedException("Solo puedes eliminar tus propias reseñas."))
                .when(resenaService).eliminar("r1");
        mockMvc.perform(delete("/api/v1/resenas/r1").with(user("pedro@test.com").roles("CLIENTE")))
                .andExpect(status().isForbidden());
    }
}
