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

import com.restaurante.validator.PlatoValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceImplTest {
    @Mock private PedidoRepositoryJPA pedidoRepository;
    @Mock private PlatoRepositoryJPA platoRepository;
    @Mock private EventoPedidoRepositoryMongo eventoMongoRepository;
    @Spy private ItemPedidoEntityMapper itemMapper = new ItemPedidoEntityMapperImpl();
    @Spy private PedidoEntityMapper pedidoEntityMapper = new PedidoEntityMapperImpl(new ItemPedidoEntityMapperImpl());
    @Spy private PlatoEntityMapper platoEntityMapper = new PlatoEntityMapperImpl();
    @Spy private PedidoMapperIn pedidoMapperIn = new PedidoMapperInImpl();
    @Spy private PedidoMapperOut pedidoMapperOut = new PedidoMapperOutImpl();
    @Mock private PlatoValidator platoValidator;
    @InjectMocks private PedidoServiceImpl pedidoService;

    @Test
    void obtenerPedidoPorId_exitoso() {
        mockSecurityContext();
        PedidoEntity pe = new PedidoEntity(); pe.setId(1L); pe.setItems(List.of()); pe.setEstado(com.restaurante.model.domain.EstadoPedido.EN_PREPARACION);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pe));
        assertDoesNotThrow(() -> pedidoService.obtenerPedidoPorId(1L));
    }

    @Test
    void crearPedido_exitoso() {
        mockSecurityContext();
        PedidoRequestDTO dto = new PedidoRequestDTO(1L, List.of(new ItemPedidoRequestDTO(1L, 2, List.of())));
        PlatoEntity plato = new PlatoEntity(); plato.setId(1L); plato.setPrecio(10.0);
        when(platoRepository.findById(1L)).thenReturn(Optional.of(plato));
        
        PedidoEntity pe = new PedidoEntity(); pe.setId(1L); pe.setItems(List.of()); pe.setEstado(com.restaurante.model.domain.EstadoPedido.RECIBIDO);
        when(pedidoRepository.save(any())).thenReturn(pe);
        
        assertNotNull(pedidoService.crearPedido(dto));
    }

    @Test
    void cambiarEstado_exitoso() {
        mockSecurityContext();
        PedidoEntity pe = new PedidoEntity(); pe.setId(1L); pe.setItems(List.of()); pe.setEstado(com.restaurante.model.domain.EstadoPedido.RECIBIDO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pe));
        when(pedidoRepository.save(any())).thenReturn(pe);
        
        assertNotNull(pedidoService.cambiarEstado(1L, new CambioEstadoRequestDTO(com.restaurante.model.domain.EstadoPedido.EN_PREPARACION, "motivo")));
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
        mockSecurityContext();
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());
        CambioEstadoRequestDTO req = new CambioEstadoRequestDTO(com.restaurante.model.domain.EstadoPedido.EN_PREPARACION, "1");
        assertThrows(PedidoNotFoundException.class, () -> pedidoService.cambiarEstado(99L, req));
    }

    @Test
    void eliminar_lanzaExcepcionSiNoExiste() {
        when(pedidoRepository.existsById(99L)).thenReturn(false);
        assertThrows(PedidoNotFoundException.class, () -> pedidoService.eliminar(99L));
    }

    private void mockSecurityContext() {
        org.springframework.security.core.Authentication auth = mock(org.springframework.security.core.Authentication.class);
        lenient().when(auth.getName()).thenReturn("admin");
        lenient().when(auth.getAuthorities()).thenReturn((java.util.Collection) List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN")));
        org.springframework.security.core.context.SecurityContext context = mock(org.springframework.security.core.context.SecurityContext.class);
        lenient().when(context.getAuthentication()).thenReturn(auth);
        org.springframework.security.core.context.SecurityContextHolder.setContext(context);
    }
}
