package com.restaurante.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.model.dto.request.CambioEstadoRequestDTO;
import com.restaurante.model.dto.request.ItemPedidoRequestDTO;
import com.restaurante.model.dto.request.PedidoRequestDTO;
import com.restaurante.model.dto.response.PedidoResponseDTO;
import com.restaurante.service.PedidoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PedidoController.class)
@org.springframework.context.annotation.Import({com.restaurante.security.SecurityConfig.class, com.restaurante.security.JwtAuthFilter.class})
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @MockitoBean
    private PedidoService pedidoService;

    @MockitoBean
    private com.restaurante.security.JwtUtil jwtUtil;

    @MockitoBean
    private com.restaurante.service.UsuarioDetailsService usuarioDetailsService;

    @Test
    void crearPedido_exitoso() throws Exception {
        UUID id = UUID.randomUUID();
        PedidoRequestDTO req = new PedidoRequestDTO(1L, List.of(new ItemPedidoRequestDTO(1L, 1, List.of())));
        PedidoResponseDTO res = new PedidoResponseDTO(id, 1L, com.restaurante.model.domain.EstadoPedido.EN_PREPARACION, List.of(), LocalDateTime.now());

        when(pedidoService.crearPedido(any(PedidoRequestDTO.class))).thenReturn(res);

        mockMvc.perform(post("/api/v1/pedidos")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("mesero").roles("MESERO"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void obtenerPorId_exitoso() throws Exception {
        UUID id = UUID.randomUUID();
        PedidoResponseDTO res = new PedidoResponseDTO(id, 1L, com.restaurante.model.domain.EstadoPedido.EN_PREPARACION, List.of(), LocalDateTime.now());
        when(pedidoService.obtenerPedidoPorId(id)).thenReturn(res);

        mockMvc.perform(get("/api/v1/pedidos/" + id)
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void listarTodos_exitoso() throws Exception {
        when(pedidoService.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/pedidos")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void tableroCocina_exitoso() throws Exception {
        when(pedidoService.listarPorEstado(com.restaurante.model.domain.EstadoPedido.EN_PREPARACION)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/pedidos/cocina")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("chef").roles("CHEF"))
                .param("estado", "EN_PREPARACION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void cambiarEstado_exitoso() throws Exception {
        UUID id = UUID.randomUUID();
        CambioEstadoRequestDTO req = new CambioEstadoRequestDTO(com.restaurante.model.domain.EstadoPedido.LISTO, "Test");
        PedidoResponseDTO res = new PedidoResponseDTO(id, 1L, com.restaurante.model.domain.EstadoPedido.LISTO, List.of(), LocalDateTime.now());
        
        when(pedidoService.cambiarEstado(eq(id), any(CambioEstadoRequestDTO.class))).thenReturn(res);

        mockMvc.perform(patch("/api/v1/pedidos/" + id + "/estado")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("chef").roles("CHEF"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void eliminarPedido_exitoso() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(delete("/api/v1/pedidos/" + id)
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());
        verify(pedidoService).eliminar(id);
    }
}
