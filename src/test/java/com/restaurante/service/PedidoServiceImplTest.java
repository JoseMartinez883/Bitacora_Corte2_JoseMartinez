package com.restaurante.service;

import com.restaurante.exception.PedidoNotFoundException;
import com.restaurante.exception.PlatoNotFoundException;
import com.restaurante.model.dto.request.CambioEstadoRequestDTO;
import com.restaurante.model.dto.request.ItemPedidoRequestDTO;
import com.restaurante.model.dto.request.PedidoRequestDTO;
import java.util.Optional;
import java.util.List;
import com.restaurante.mapper.*;
import com.restaurante.persistence.entity.*;
import com.restaurante.repository.*;
import com.restaurante.repository.*;
import com.restaurante.validator.PlatoValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    void crearPedido_lanzaExcepcionSiPlatoNoExiste() {
        PedidoRequestDTO dto = new PedidoRequestDTO(1L, List.of(new ItemPedidoRequestDTO(99L, 2, List.of("Sin sal"))));
        when(platoRepository.findById(99L)).thenReturn(Optional.empty());
        
        assertThrows(PlatoNotFoundException.class, () -> pedidoService.crearPedido(dto));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void obtenerPedidoPorId_lanzaExcepcionSiNoExiste() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(PedidoNotFoundException.class, () -> pedidoService.obtenerPedidoPorId(99L));
    }

    @Test
    void cambiarEstado_lanzaExcepcionSiNoExiste() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(PedidoNotFoundException.class, () -> pedidoService.cambiarEstado(99L, new CambioEstadoRequestDTO(com.restaurante.model.domain.EstadoPedido.EN_PREPARACION, "1")));
    }

    @Test
    void eliminar_lanzaExcepcionSiNoExiste() {
        when(pedidoRepository.existsById(99L)).thenReturn(false);
        assertThrows(PedidoNotFoundException.class, () -> pedidoService.eliminar(99L));
    }
}
