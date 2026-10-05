package com.restaurante.service;

import com.restaurante.mapper.*;
import com.restaurante.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CuentaServiceImplTest {
    @Mock private CuentaRepositoryJPA cuentaRepository;
    @Mock private MesaRepositoryJPA mesaRepository;
    @Spy private CuentaEntityMapper cuentaEntityMapper = new CuentaEntityMapper(new ItemPedidoEntityMapper());
    @Spy private MesaEntityMapper mesaEntityMapper = new MesaEntityMapper();
    @Mock private CuentaMapperOut cuentaMapperOut;
    @InjectMocks private CuentaServiceImpl cuentaService;

    @Test
    void init() { assertNotNull(cuentaService); }
}
