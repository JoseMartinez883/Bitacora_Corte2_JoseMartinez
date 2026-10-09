package com.restaurante.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.exception.CatalogoNotFoundException;
import com.restaurante.model.dto.request.CatalogoRequestDTO;
import com.restaurante.model.dto.response.CatalogoResponseDTO;
import com.restaurante.service.CatalogoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CatalogoController.class)
@org.springframework.context.annotation.Import({com.restaurante.security.SecurityConfig.class, com.restaurante.security.JwtAuthFilter.class})
class CatalogoControllerTest {

    @Autowired private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean private CatalogoService catalogoService;
    @MockitoBean private com.restaurante.security.JwtUtil jwtUtil;
    @MockitoBean private com.restaurante.security.CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    @MockitoBean private com.restaurante.service.UsuarioDetailsService usuarioDetailsService;

    private CatalogoRequestDTO req() {
        return new CatalogoRequestDTO(1L, List.of("http://img/1.jpg"), List.of("vegano"));
    }

    private CatalogoResponseDTO res() {
        return new CatalogoResponseDTO("c1", 1L, List.of("http://img/1.jpg"), List.of("vegano"));
    }

    @Test
    void crear_comoChef_exitoso() throws Exception {
        when(catalogoService.crearCatalogo(any(CatalogoRequestDTO.class))).thenReturn(res());
        mockMvc.perform(post("/api/v1/catalogos")
                        .with(user("chef@test.com").roles("CHEF"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req())))
                .andExpect(status().isCreated());
    }

    @Test
    void crear_sinImagenes_retorna400() throws Exception {
        mockMvc.perform(post("/api/v1/catalogos")
                        .with(user("admin@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CatalogoRequestDTO(1L, List.of(), List.of()))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crear_comoCliente_prohibido() throws Exception {
        mockMvc.perform(post("/api/v1/catalogos")
                        .with(user("cliente@test.com").roles("CLIENTE"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req())))
                .andExpect(status().isForbidden());
        verify(catalogoService, never()).crearCatalogo(any());
    }

    @Test
    void listar_exitoso() throws Exception {
        when(catalogoService.listarTodos()).thenReturn(List.of(res()));
        mockMvc.perform(get("/api/v1/catalogos").with(user("cliente@test.com").roles("CLIENTE")))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerPorPlato_exitoso() throws Exception {
        when(catalogoService.obtenerPorIdPlato(1L)).thenReturn(res());
        mockMvc.perform(get("/api/v1/catalogos/plato/1").with(user("cliente@test.com").roles("CLIENTE")))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerPorPlato_noExiste_retorna404() throws Exception {
        when(catalogoService.obtenerPorIdPlato(99L)).thenThrow(new CatalogoNotFoundException("no existe"));
        mockMvc.perform(get("/api/v1/catalogos/plato/99").with(user("cliente@test.com").roles("CLIENTE")))
                .andExpect(status().isNotFound());
    }

    @Test
    void actualizar_comoAdmin_exitoso() throws Exception {
        when(catalogoService.actualizar(eq("c1"), any(CatalogoRequestDTO.class))).thenReturn(res());
        mockMvc.perform(put("/api/v1/catalogos/c1")
                        .with(user("admin@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req())))
                .andExpect(status().isOk());
    }

    @Test
    void eliminar_comoAdmin_exitoso() throws Exception {
        mockMvc.perform(delete("/api/v1/catalogos/c1").with(user("admin@test.com").roles("ADMIN")))
                .andExpect(status().isNoContent());
        verify(catalogoService).eliminar("c1");
    }
}
