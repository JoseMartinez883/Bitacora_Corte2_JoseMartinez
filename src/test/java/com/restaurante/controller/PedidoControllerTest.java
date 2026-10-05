package com.restaurante.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.model.dto.request.PedidoRequestDTO;
import com.restaurante.model.dto.request.ItemPedidoRequestDTO;
import com.restaurante.model.dto.response.PedidoResponseDTO;
import com.restaurante.service.PedidoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PedidoController.class)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper().registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @MockitoBean
    private PedidoService pedidoService;

    @Test
    void crearPedido_exitoso() throws Exception {
        PedidoRequestDTO req = new PedidoRequestDTO(1L, List.of(new ItemPedidoRequestDTO(1L, 1, List.of())));
        PedidoResponseDTO res = new PedidoResponseDTO(1L, 1L, com.restaurante.model.domain.EstadoPedido.EN_PREPARACION, List.of(), java.time.LocalDateTime.now());

        when(pedidoService.crearPedido(any(PedidoRequestDTO.class))).thenReturn(res);

        mockMvc.perform(post("/api/v1/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void obtenerPorId_exitoso() throws Exception {
        PedidoResponseDTO res = new PedidoResponseDTO(1L, 1L, com.restaurante.model.domain.EstadoPedido.EN_PREPARACION, List.of(), java.time.LocalDateTime.now());
        when(pedidoService.obtenerPedidoPorId(1L)).thenReturn(res);

        mockMvc.perform(get("/api/v1/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
