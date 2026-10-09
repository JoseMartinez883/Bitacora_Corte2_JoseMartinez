package com.restaurante.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityExceptionsTest {
    @Autowired 
    private MockMvc mockMvc;
    
    @MockitoBean
    private com.restaurante.repository.EventoPedidoRepositoryMongo eventoPedidoRepositoryMongo;
    
    @Test
    void testUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/pedidos")).andExpect(status().isUnauthorized());
    }
    
    @Test
    @org.springframework.security.test.context.support.WithMockUser(roles = "CLIENTE")
    void testForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/pedidos/cocina").param("estado", "RECIBIDO")).andExpect(status().isForbidden());
    }
}
