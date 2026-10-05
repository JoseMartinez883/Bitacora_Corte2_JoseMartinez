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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PedidoController.class)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @MockitoBean
    private PedidoService pedidoService;

    @Test
    void crearPedido_exitoso() throws Exception {
        PedidoRequestDTO req = new PedidoRequestDTO(1L, List.of(new ItemPedidoRequestDTO(1L, 1, List.of())));
        PedidoResponseDTO res = new PedidoResponseDTO(1L, 1L, com.restaurante.model.domain.EstadoPedido.EN_PREPARACION, List.of(), LocalDateTime.now());

        when(pedidoService.crearPedido(any(PedidoRequestDTO.class))).thenReturn(res);

        mockMvc.perform(post("/api/v1/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void obtenerPorId_exitoso() throws Exception {
        PedidoResponseDTO res = new PedidoResponseDTO(1L, 1L, com.restaurante.model.domain.EstadoPedido.EN_PREPARACION, List.of(), LocalDateTime.now());
        when(pedidoService.obtenerPedidoPorId(1L)).thenReturn(res);

        mockMvc.perform(get("/api/v1/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void listarTodos_exitoso() throws Exception {
        when(pedidoService.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void tableroCocina_exitoso() throws Exception {
        when(pedidoService.listarPorEstado(com.restaurante.model.domain.EstadoPedido.EN_PREPARACION)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/pedidos/cocina")
                .param("estado", "EN_PREPARACION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void cambiarEstado_exitoso() throws Exception {
        CambioEstadoRequestDTO req = new CambioEstadoRequestDTO(com.restaurante.model.domain.EstadoPedido.LISTO, "Test");
        PedidoResponseDTO res = new PedidoResponseDTO(1L, 1L, com.restaurante.model.domain.EstadoPedido.LISTO, List.of(), LocalDateTime.now());
        
        when(pedidoService.cambiarEstado(eq(1L), any(CambioEstadoRequestDTO.class))).thenReturn(res);

        mockMvc.perform(patch("/api/v1/pedidos/1/estado")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void eliminarPedido_exitoso() throws Exception {
        mockMvc.perform(delete("/api/v1/pedidos/1"))
                .andExpect(status().isNoContent());
        verify(pedidoService).eliminar(1L);
    }
}
