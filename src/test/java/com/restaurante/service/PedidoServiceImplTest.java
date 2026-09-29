package com.restaurante.service;

import com.restaurante.mapper.*;
import com.restaurante.model.domain.*;
import com.restaurante.persistence.entity.*;
import com.restaurante.model.dto.request.*;
import com.restaurante.repository.*;
import com.restaurante.validator.PlatoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceImplTest {
    @Mock private PedidoRepositoryJPA pedidoRepository;
    @Mock private PlatoRepositoryJPA platoRepository;
    @Mock private EventoPedidoRepositoryMongo eventoMongoRepository;
    @Spy private ItemPedidoEntityMapper itemMapper = new ItemPedidoEntityMapper();
    @Spy private PedidoEntityMapper pedidoEntityMapper = new PedidoEntityMapper(new ItemPedidoEntityMapper());
    @Spy private PlatoEntityMapper platoEntityMapper = new PlatoEntityMapper();
    @Mock private PedidoMapperIn pedidoMapperIn;
    @Mock private PedidoMapperOut pedidoMapperOut;
    @Mock private PlatoValidator platoValidator;
    @InjectMocks private PedidoServiceImpl pedidoService;

    @Test
    void obtenerPedidoPorId_exitoso() {
        PedidoEntity pe = new PedidoEntity(); pe.setId(1L);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pe));
        assertDoesNotThrow(() -> pedidoService.obtenerPedidoPorId(1L));
    }
}
