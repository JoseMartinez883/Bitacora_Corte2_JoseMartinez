package com.restaurante.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.model.dto.response.MesaResponseDTO;
import com.restaurante.service.MesaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MesaController.class)
class MesaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MesaService mesaService;

    @Test
    void listarTodas_exitoso() throws Exception {
        when(mesaService.listarTodas()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/mesas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void obtenerPorId_exitoso() throws Exception {
        MesaResponseDTO res = new MesaResponseDTO(1L, 1, 4, com.restaurante.model.domain.EstadoMesa.DISPONIBLE, false);
        when(mesaService.obtenerMesaPorId(1L)).thenReturn(res);

        mockMvc.perform(get("/api/v1/mesas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
