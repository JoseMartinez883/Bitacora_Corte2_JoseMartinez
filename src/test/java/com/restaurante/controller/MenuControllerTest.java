package com.restaurante.controller;

import com.restaurante.model.dto.response.PlatoResponseDTO;
import com.restaurante.service.PlatoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MenuController.class)
@org.springframework.context.annotation.Import({com.restaurante.security.SecurityConfig.class, com.restaurante.security.JwtAuthFilter.class})
class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlatoService platoService;

    @MockitoBean
    private com.restaurante.security.JwtUtil jwtUtil;

    @MockitoBean
    private com.restaurante.service.UsuarioDetailsService usuarioDetailsService;

    @Test
    void obtenerMenu_exitoso() throws Exception {
        PlatoResponseDTO p1 = new PlatoResponseDTO(1L, "Pizza", 10.0, "Principal", "Masa fina", "Salsa roja", List.of(), List.of(), List.of(), "Rica", true, true);
        when(platoService.listarDisponibles()).thenReturn(List.of(p1));

        mockMvc.perform(get("/api/v1/menu")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("CLIENTE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nombre").value("Pizza"));
    }
}
