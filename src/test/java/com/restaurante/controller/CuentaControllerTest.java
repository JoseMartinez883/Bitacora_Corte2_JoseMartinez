package com.restaurante.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.model.dto.request.PagoCuentaRequestDTO;
import com.restaurante.model.dto.response.CuentaResponseDTO;
import com.restaurante.service.CuentaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CuentaController.class)
class CuentaControllerTest {

    @Autowired
    private MockMvc mockMvc;



    @MockitoBean
    private CuentaService cuentaService;

    @Test
    void obtenerCuentaActiva_exitoso() throws Exception {
        CuentaResponseDTO res = new CuentaResponseDTO(1L, 1L, com.restaurante.model.domain.EstadoCuenta.ABIERTA, java.util.List.of(), 50.0, null, null, null);
        when(cuentaService.obtenerCuentaActivaPorMesa(1L)).thenReturn(res);

        mockMvc.perform(get("/api/v1/cuentas/mesa/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
