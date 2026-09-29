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
class MesaServiceImplTest {
    @Mock private MesaRepositoryJPA mesaRepository;
    @Spy private MesaEntityMapper mesaEntityMapper = new MesaEntityMapper();
    @Mock private MesaMapperOut mesaMapperOut;
    @InjectMocks private MesaServiceImpl mesaService;

    @Test
    void init() { assertNotNull(mesaService); }
}
