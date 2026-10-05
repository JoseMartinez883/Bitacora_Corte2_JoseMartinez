package com.restaurante.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.service.RegistroVehiculoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehiculoController.class)
class VehiculoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegistroVehiculoService registroVehiculoService;

    @Test
    void listarTodos_exitoso() throws Exception {
        when(registroVehiculoService.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/vehiculos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
