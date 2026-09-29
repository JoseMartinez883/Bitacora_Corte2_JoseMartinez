package com.restaurante.service;

import com.restaurante.exception.PedidoNotFoundException;
import com.restaurante.exception.TransicionEstadoInvalidaException;
import com.restaurante.mapper.PedidoMapperIn;
import com.restaurante.mapper.PedidoMapperOut;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.Pedido;
import com.restaurante.model.dto.request.CambioEstadoRequestDTO;
import com.restaurante.model.dto.response.PedidoResponseDTO;
import com.restaurante.repository.PedidoRepository;
import com.restaurante.repository.PlatoRepository;
import com.restaurante.validator.PlatoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceImplTest {

    @Mock private PedidoRepository pedidoRepository;
    @Mock private PlatoRepository platoRepository;
    @Mock private PedidoMapperIn pedidoMapperIn;
    @Mock private PedidoMapperOut pedidoMapperOut;
    @Mock private PlatoValidator platoValidator;

    @InjectMocks private PedidoServiceImpl pedidoService;

    private Pedido pedidoMock;
    private PedidoResponseDTO responseMock;

    @BeforeEach
    void setUp() {
        pedidoMock = new Pedido();
        pedidoMock.setId(1L);
        pedidoMock.setIdMesa(1L);
        pedidoMock.setEstado(EstadoPedido.RECIBIDO);
        pedidoMock.setTimestamp(LocalDateTime.now());

        responseMock = new PedidoResponseDTO(
                1L, 1L, EstadoPedido.RECIBIDO, Collections.emptyList(), LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("1. Happy Path: obtenerPedidoPorId retorna el pedido correctamente")
    void obtenerPedidoPorId_Existente_RetornaPedido() {
        when(pedidoRepository.buscarPorId(1L)).thenReturn(Optional.of(pedidoMock));
        when(pedidoMapperOut.toResponse(pedidoMock)).thenReturn(responseMock);

        PedidoResponseDTO resultado = pedidoService.obtenerPedidoPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        verify(pedidoRepository).buscarPorId(1L);
    }

    @Test
    @DisplayName("2. No encontrado: obtenerPedidoPorId con ID inexistente")
    void obtenerPedidoPorId_Inexistente_LanzaPedidoNotFoundException() {
        when(pedidoRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(PedidoNotFoundException.class,
                () -> pedidoService.obtenerPedidoPorId(99L));
    }

    @Test
    @DisplayName("3. Conflicto: cambiarEstado de pedido que no existe")
    void cambiarEstado_PedidoNoExistente_LanzaExcepcion() {
        CambioEstadoRequestDTO dto = new CambioEstadoRequestDTO(EstadoPedido.EN_PREPARACION, "operario-01");
        when(pedidoRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(PedidoNotFoundException.class,
                () -> pedidoService.cambiarEstado(99L, dto));
    }

    @Test
    @DisplayName("4. Estado inválido: saltar de RECIBIDO directo a ENTREGADO")
    void cambiarEstado_TransicionInvalida_LanzaTransicionEstadoInvalidaException() {
        CambioEstadoRequestDTO dto = new CambioEstadoRequestDTO(EstadoPedido.ENTREGADO, "operario-01");
        when(pedidoRepository.buscarPorId(1L)).thenReturn(Optional.of(pedidoMock));

        assertThrows(TransicionEstadoInvalidaException.class,
                () -> pedidoService.cambiarEstado(1L, dto));
    }

    @Test
    @DisplayName("5. Lista vacía: listarTodos retorna lista vacía, no null")
    void listarTodos_SinPedidos_RetornaListaVacia() {
        when(pedidoRepository.buscarTodos()).thenReturn(Collections.emptyList());

        List<PedidoResponseDTO> resultado = pedidoService.listarTodos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}
