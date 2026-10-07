package com.restaurante.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@org.springframework.test.context.ActiveProfiles("test")
class SecurityTest {

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private com.restaurante.repository.EventoPedidoRepositoryMongo eventoPedidoRepositoryMongo;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private com.restaurante.repository.CatalogoRepositoryMongo catalogoRepositoryMongo;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private com.restaurante.repository.ResenaRepositoryMongo resenaRepositoryMongo;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private com.restaurante.security.CustomOAuth2SuccessHandler oAuth2SuccessHandler;

    @Autowired
    private MockMvc mockMvc;

    // Prueba 1: Verificar que ninguno de estos endpoints deje entrar sin token (401)
    @ParameterizedTest
    @ValueSource(strings = {
            "/api/v1/platos",
            "/api/v1/mesas",
            "/api/v1/reservas",
            "/api/v1/pedidos",
            "/api/v1/vehiculos"
    })
    void endpointsProtegidos_sinToken_devuelven401(String endpoint) throws Exception {
        mockMvc.perform(get(endpoint))
                .andExpect(status().isUnauthorized());
    }

    // Prueba 2: Verificar que un rol CLIENTE no pueda crear platos, vehículos ni abrir cuenta de mesa (403)
    @Test
    @WithMockUser(roles = "CLIENTE")
    void crearPlato_conRolCliente_devuelve403() throws Exception {
        String json = "{\"nombre\":\"Pizza Test\",\"precio\":10000.0,\"categoria\":\"PIZZA\",\"masa\":\"Tradicional\",\"salsa\":\"Tomate\"}";
        mockMvc.perform(post("/api/v1/platos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void registrarVehiculo_conRolCliente_devuelve403() throws Exception {
        String json = "{\"placa\":\"ABC123\",\"tipoVehiculo\":\"CARRO\"}";
        mockMvc.perform(post("/api/v1/vehiculos/entrada")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void abrirCuentaMesa_conRolCliente_devuelve403() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/api/v1/mesas/1/abrir-cuenta"))
                .andExpect(status().isForbidden());
    }

    @Test
    void endpointProtegido_conTokenInvalido_devuelve401() throws Exception {
        mockMvc.perform(get("/api/v1/platos")
                .header("Authorization", "Bearer tokenbasura"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_conCredencialesIncorrectas_devuelve401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"nadie@bellaciao.com\",\"password\":\"incorrecta\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void respuestas_incluyenCabecerasDeSeguridad() throws Exception {
        mockMvc.perform(get("/api/v1/menu"))
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().exists("Content-Security-Policy"));
    }

    @Test
    void menu_esPublico_sinToken() throws Exception {
        mockMvc.perform(get("/api/v1/menu"))
                .andExpect(status().isOk());
    }
}
