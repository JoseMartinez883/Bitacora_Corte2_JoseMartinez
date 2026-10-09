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

import java.util.UUID;

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
    @Mock private com.restaurante.repository.IngredienteRepositoryJPA ingredienteRepository;
    @InjectMocks private PedidoServiceImpl pedidoService;

    @Test
    void obtenerPedidoPorId_exitoso() {
        mockSecurityContext();
        UUID id = UUID.randomUUID();
        PedidoEntity pe = new PedidoEntity(); pe.setId(id); pe.setItems(List.of()); pe.setEstado(com.restaurante.model.domain.EstadoPedido.EN_PREPARACION);
        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pe));
        assertDoesNotThrow(() -> pedidoService.obtenerPedidoPorId(id));
    }

    @Test
    void crearPedido_exitoso() {
        mockSecurityContext();
        UUID id = UUID.randomUUID();
        PedidoRequestDTO dto = new PedidoRequestDTO(1L, List.of(new ItemPedidoRequestDTO(1L, 2, List.of())));
        PlatoEntity plato = new PlatoEntity(); plato.setId(1L); plato.setPrecio(10.0);
        when(platoRepository.findById(1L)).thenReturn(Optional.of(plato));
        
        PedidoEntity pe = new PedidoEntity(); pe.setId(id); pe.setItems(List.of()); pe.setEstado(com.restaurante.model.domain.EstadoPedido.RECIBIDO);
        when(pedidoRepository.save(any())).thenReturn(pe);
        
        assertNotNull(pedidoService.crearPedido(dto));
    }

    @Test
    void cambiarEstado_exitoso() {
        mockSecurityContext();
        UUID id = UUID.randomUUID();
        PedidoEntity pe = new PedidoEntity(); pe.setId(id); pe.setItems(List.of()); pe.setEstado(com.restaurante.model.domain.EstadoPedido.RECIBIDO);
        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pe));
        when(pedidoRepository.save(any())).thenReturn(pe);
        
        assertNotNull(pedidoService.cambiarEstado(id, new CambioEstadoRequestDTO(com.restaurante.model.domain.EstadoPedido.EN_PREPARACION, "motivo")));
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
        UUID id = UUID.randomUUID();
        when(pedidoRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(PedidoNotFoundException.class, () -> pedidoService.obtenerPedidoPorId(id));
    }

    @Test
    void cambiarEstado_lanzaExcepcionSiNoExiste() {
        mockSecurityContext();
        UUID id = UUID.randomUUID();
        when(pedidoRepository.findById(id)).thenReturn(Optional.empty());
        CambioEstadoRequestDTO req = new CambioEstadoRequestDTO(com.restaurante.model.domain.EstadoPedido.EN_PREPARACION, "1");
        assertThrows(PedidoNotFoundException.class, () -> pedidoService.cambiarEstado(id, req));
    }

    @Test
    void eliminar_lanzaExcepcionSiNoExiste() {
        UUID id = UUID.randomUUID();
        when(pedidoRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(PedidoNotFoundException.class, () -> pedidoService.eliminar(id));
    }

    @Test
    void eliminar_exitosoCuandoEstadoEsRecibido() {
        UUID id = UUID.randomUUID();
        PedidoEntity entity = new PedidoEntity();
        entity.setId(id);
        entity.setEstado(com.restaurante.model.domain.EstadoPedido.RECIBIDO);
        when(pedidoRepository.findById(id)).thenReturn(Optional.of(entity));

        assertDoesNotThrow(() -> pedidoService.eliminar(id));
        verify(pedidoRepository).deleteById(id);
    }

    @Test
    void eliminar_lanzaExcepcionSiYaEstaEnCocina() {
        UUID id = UUID.randomUUID();
        PedidoEntity entity = new PedidoEntity();
        entity.setId(id);
        entity.setEstado(com.restaurante.model.domain.EstadoPedido.EN_PREPARACION);
        when(pedidoRepository.findById(id)).thenReturn(Optional.of(entity));

        assertThrows(com.restaurante.exception.PedidoEnCocinaException.class, () -> pedidoService.eliminar(id));
        verify(pedidoRepository, never()).deleteById(id);
    }

    @Test
    void listarTodos_exitoso() {
        when(pedidoRepository.findAll()).thenReturn(List.of(new PedidoEntity()));
        var lista = pedidoService.listarTodos();
        assertFalse(lista.isEmpty());
    }

    @Test
    void listarPorEstado_exitoso() {
        when(pedidoRepository.findByEstado(com.restaurante.model.domain.EstadoPedido.RECIBIDO)).thenReturn(List.of(new PedidoEntity()));
        var lista = pedidoService.listarPorEstado(com.restaurante.model.domain.EstadoPedido.RECIBIDO);
        assertFalse(lista.isEmpty());
    }

    @Test
    void eliminarItemPedido_exitoso() {
        UUID id = UUID.randomUUID();
        PedidoEntity entity = new PedidoEntity();
        entity.setId(id);
        entity.setEstado(com.restaurante.model.domain.EstadoPedido.RECIBIDO);
        ItemPedidoEntity item = new ItemPedidoEntity();
        item.setId(1L);
        entity.setItems(new java.util.ArrayList<>(List.of(item)));
        
        when(pedidoRepository.findById(id)).thenReturn(Optional.of(entity));
        when(pedidoRepository.save(any())).thenReturn(entity);
        
        assertDoesNotThrow(() -> pedidoService.eliminarItemPedido(id, 1L));
    }

    @Test
    void eliminarItemPedido_fallaPorEstado() {
        UUID id = UUID.randomUUID();
        PedidoEntity entity = new PedidoEntity();
        entity.setId(id);
        entity.setEstado(com.restaurante.model.domain.EstadoPedido.EN_PREPARACION);
        
        when(pedidoRepository.findById(id)).thenReturn(Optional.of(entity));
        
        assertThrows(IllegalStateException.class, () -> pedidoService.eliminarItemPedido(id, 1L));
    }

    @Test
    void cambiarEstado_fallaPermisos() {
        org.springframework.security.core.Authentication auth = mock(org.springframework.security.core.Authentication.class);
        lenient().when(auth.getAuthorities()).thenReturn((java.util.Collection) List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_CLIENTE")));
        org.springframework.security.core.context.SecurityContext context = mock(org.springframework.security.core.context.SecurityContext.class);
        lenient().when(context.getAuthentication()).thenReturn(auth);
        org.springframework.security.core.context.SecurityContextHolder.setContext(context);
        
        UUID id = UUID.randomUUID();
        
        CambioEstadoRequestDTO reqPrep = new CambioEstadoRequestDTO(com.restaurante.model.domain.EstadoPedido.EN_PREPARACION, "1");
        assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> pedidoService.cambiarEstado(id, reqPrep));

        CambioEstadoRequestDTO reqListo = new CambioEstadoRequestDTO(com.restaurante.model.domain.EstadoPedido.LISTO, "1");
        assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> pedidoService.cambiarEstado(id, reqListo));

        CambioEstadoRequestDTO reqEntregado = new CambioEstadoRequestDTO(com.restaurante.model.domain.EstadoPedido.ENTREGADO, "1");
        assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> pedidoService.cambiarEstado(id, reqEntregado));
    }

    @Test
    void cambiarEstado_permisosExitosos() {
        UUID id = UUID.randomUUID();
        
        PedidoEntity pe1 = new PedidoEntity(); pe1.setId(id); pe1.setItems(List.of()); pe1.setEstado(com.restaurante.model.domain.EstadoPedido.RECIBIDO);
        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pe1));
        when(pedidoRepository.save(any())).thenReturn(pe1);

        mockSecurityContextWithRole("ROLE_CHEF");
        assertDoesNotThrow(() -> pedidoService.cambiarEstado(id, new CambioEstadoRequestDTO(com.restaurante.model.domain.EstadoPedido.EN_PREPARACION, "Chef")));

        PedidoEntity pe2 = new PedidoEntity(); pe2.setId(id); pe2.setItems(List.of()); pe2.setEstado(com.restaurante.model.domain.EstadoPedido.EN_PREPARACION);
        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pe2));
        when(pedidoRepository.save(any())).thenReturn(pe2);
        
        mockSecurityContextWithRole("ROLE_CHEF");
        assertDoesNotThrow(() -> pedidoService.cambiarEstado(id, new CambioEstadoRequestDTO(com.restaurante.model.domain.EstadoPedido.LISTO, "Chef")));

        PedidoEntity pe3 = new PedidoEntity(); pe3.setId(id); pe3.setItems(List.of()); pe3.setEstado(com.restaurante.model.domain.EstadoPedido.LISTO);
        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pe3));
        when(pedidoRepository.save(any())).thenReturn(pe3);

        mockSecurityContextWithRole("ROLE_MESERO");
        assertDoesNotThrow(() -> pedidoService.cambiarEstado(id, new CambioEstadoRequestDTO(com.restaurante.model.domain.EstadoPedido.ENTREGADO, "Mesero")));

        PedidoEntity pe4 = new PedidoEntity(); pe4.setId(id); pe4.setItems(List.of()); pe4.setEstado(com.restaurante.model.domain.EstadoPedido.LISTO);
        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pe4));
        when(pedidoRepository.save(any())).thenReturn(pe4);

        mockSecurityContextWithRole("ROLE_ADMIN");
        assertDoesNotThrow(() -> pedidoService.cambiarEstado(id, new CambioEstadoRequestDTO(com.restaurante.model.domain.EstadoPedido.ENTREGADO, "Admin")));
    }

    @Test
    void crearPedido_descuentaInventarioYDesactivaPlatos() {
        mockSecurityContextWithRole("ROLE_ADMIN");
        UUID id = UUID.randomUUID();
        PedidoRequestDTO dto = new PedidoRequestDTO(1L, List.of(new ItemPedidoRequestDTO(1L, 2, List.of("Queso"))));
        
        PlatoEntity plato = new PlatoEntity(); 
        plato.setId(1L); 
        plato.setPrecio(10.0);
        plato.setMasa("Normal");
        plato.setToppings(List.of("Queso"));
        plato.setProteinas(List.of("Carne"));
        plato.setSalsasExtras(List.of("BBQ"));
        
        when(platoRepository.findById(1L)).thenReturn(Optional.of(plato));
        
        com.restaurante.persistence.entity.IngredienteEntity ingNormal = new com.restaurante.persistence.entity.IngredienteEntity();
        ingNormal.setNombre("Normal");
        ingNormal.setCantidadDisponible(1);
        
        when(ingredienteRepository.findByNombre(anyString())).thenReturn(Optional.of(ingNormal));
        when(platoRepository.findAll()).thenReturn(List.of(plato));
        
        PedidoEntity pe = new PedidoEntity(); pe.setId(id); pe.setItems(List.of()); pe.setEstado(com.restaurante.model.domain.EstadoPedido.RECIBIDO);
        when(pedidoRepository.save(any())).thenReturn(pe);
        
        assertNotNull(pedidoService.crearPedido(dto));
        
        // Verifica que se llamó a guardar ingrediente y a guardar plato (desactivado)
        verify(ingredienteRepository, atLeastOnce()).save(any());
        verify(platoRepository, atLeastOnce()).save(any());
    }

    private void mockSecurityContextWithRole(String role) {
        org.springframework.security.core.Authentication auth = mock(org.springframework.security.core.Authentication.class);
        lenient().when(auth.getName()).thenReturn("user");
        lenient().when(auth.getAuthorities()).thenReturn((java.util.Collection) List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(role)));
        org.springframework.security.core.context.SecurityContext context = mock(org.springframework.security.core.context.SecurityContext.class);
        lenient().when(context.getAuthentication()).thenReturn(auth);
        org.springframework.security.core.context.SecurityContextHolder.setContext(context);
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
