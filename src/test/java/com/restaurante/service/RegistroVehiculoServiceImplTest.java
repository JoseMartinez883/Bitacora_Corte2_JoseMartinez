package com.restaurante.service;

import com.restaurante.mapper.*;
import com.restaurante.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RegistroVehiculoServiceImplTest {
    @Mock private RegistroVehiculoRepositoryJPA registroRepository;
    @Spy private RegistroVehiculoEntityMapper entityMapper = new RegistroVehiculoEntityMapper();
    @Mock private RegistroVehiculoMapperIn mapperIn;
    @Mock private RegistroVehiculoMapperOut mapperOut;
    @InjectMocks private RegistroVehiculoServiceImpl vehiculoService;

    @Test
    void init() { assertNotNull(vehiculoService); }
}
