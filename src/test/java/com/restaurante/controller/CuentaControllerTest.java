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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CuentaController.class)
@org.springframework.context.annotation.Import({com.restaurante.security.SecurityConfig.class, com.restaurante.security.JwtAuthFilter.class})
class CuentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @MockitoBean
    private CuentaService cuentaService;

    @MockitoBean
    private com.restaurante.security.JwtUtil jwtUtil;

    @MockitoBean
    private com.restaurante.service.UsuarioDetailsService usuarioDetailsService;

    @Test
    void obtenerCuentaActiva_exitoso() throws Exception {
        CuentaResponseDTO res = new CuentaResponseDTO(1L, 1L, com.restaurante.model.domain.EstadoCuenta.ABIERTA, List.of(), 0.0, java.time.LocalDateTime.now(), null, "EFECTIVO");
        when(cuentaService.obtenerCuentaActivaPorMesa(1L)).thenReturn(res);

        mockMvc.perform(get("/api/v1/cuentas/mesa/1")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void registrarPago_exitoso() throws Exception {
        PagoCuentaRequestDTO req = new PagoCuentaRequestDTO("EFECTIVO", 50000.0);
        CuentaResponseDTO res = new CuentaResponseDTO(1L, 1L, com.restaurante.model.domain.EstadoCuenta.CERRADA, List.of(), 50000.0, java.time.LocalDateTime.now(), java.time.LocalDateTime.now(), "EFECTIVO");
        when(cuentaService.registrarPago(eq(1L), any(PagoCuentaRequestDTO.class))).thenReturn(res);

        mockMvc.perform(post("/api/v1/cuentas/mesa/1/pago")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }
}
