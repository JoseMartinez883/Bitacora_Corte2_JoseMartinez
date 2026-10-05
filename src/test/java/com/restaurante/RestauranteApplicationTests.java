package com.restaurante;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.restaurante.repository.EventoPedidoRepositoryMongo;

@SpringBootTest
@ActiveProfiles("test")
class RestauranteApplicationTests {

    @MockitoBean
    private EventoPedidoRepositoryMongo eventoPedidoRepositoryMongo;

    @Test
    void contextLoads() {
    }
}