package com.restaurante.service;

import com.restaurante.exception.ReservaNotFoundException;
import com.restaurante.model.dto.request.ReservaRequestDTO;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
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
class ReservaServiceImplTest {
    @Mock private ReservaRepositoryJPA reservaRepository;
    @Spy private ReservaEntityMapper entityMapper = new ReservaEntityMapperImpl();
    @Spy private ReservaMapperIn reservaMapperIn = new ReservaMapperInImpl();
    @Spy private ReservaMapperOut reservaMapperOut = new ReservaMapperOutImpl();
    @InjectMocks private ReservaServiceImpl reservaService;
    
    private final UUID TEST_ID = UUID.randomUUID();
    private final UUID TEST_ID_NOT_FOUND = UUID.randomUUID();

    @Test
    void init() { assertNotNull(reservaService); }

    @Test
    void crearReserva_exitoso() {
        mockSecurityContext();
        com.restaurante.persistence.entity.ReservaEntity entity = new com.restaurante.persistence.entity.ReservaEntity();
        entity.setId(TEST_ID);
        entity.setCliente("Juan");
        entity.setActiva(true);
        when(reservaRepository.save(any())).thenReturn(entity);
        
        ReservaRequestDTO dto = new ReservaRequestDTO(1L, "Juan", LocalDateTime.now().plusDays(1), 4);
        assertNotNull(reservaService.crearReserva(dto));
    }

    @Test
    void obtenerReservaPorId_exitoso() {
        mockSecurityContext();
        com.restaurante.persistence.entity.ReservaEntity entity = new com.restaurante.persistence.entity.ReservaEntity();
        entity.setId(TEST_ID);
        when(reservaRepository.findById(TEST_ID)).thenReturn(Optional.of(entity));
        assertNotNull(reservaService.obtenerReservaPorId(TEST_ID));
    }

    @Test
    void actualizar_exitoso() {
        mockSecurityContext();
        com.restaurante.persistence.entity.ReservaEntity entity = new com.restaurante.persistence.entity.ReservaEntity();
        entity.setId(TEST_ID);
        when(reservaRepository.existsById(TEST_ID)).thenReturn(true);
        when(reservaRepository.findById(TEST_ID)).thenReturn(Optional.of(entity));
        when(reservaRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        
        ReservaRequestDTO dto = new ReservaRequestDTO(1L, "Juan", LocalDateTime.now().plusDays(1), 4);
        assertNotNull(reservaService.actualizar(TEST_ID, dto));
    }

    @Test
    void cancelarReserva_exitoso() {
        mockSecurityContext();
        com.restaurante.persistence.entity.ReservaEntity entity = new com.restaurante.persistence.entity.ReservaEntity();
        entity.setId(TEST_ID);
        entity.setActiva(true);
        when(reservaRepository.findById(TEST_ID)).thenReturn(Optional.of(entity));
        when(reservaRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        
        var response = reservaService.cancelarReserva(TEST_ID);
        assertFalse(response.activa());
        verify(reservaRepository).save(any());
    }

    @Test
    void obtenerReservaPorId_lanzaExcepcionSiNoExiste() {
        when(reservaRepository.findById(TEST_ID_NOT_FOUND)).thenReturn(Optional.empty());
        assertThrows(ReservaNotFoundException.class, () -> reservaService.obtenerReservaPorId(TEST_ID_NOT_FOUND));
    }

    @Test
    void cancelarReserva_lanzaExcepcionSiNoExiste() {
        when(reservaRepository.findById(TEST_ID_NOT_FOUND)).thenReturn(Optional.empty());
        assertThrows(ReservaNotFoundException.class, () -> reservaService.cancelarReserva(TEST_ID_NOT_FOUND));
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void actualizar_lanzaExcepcionSiNoExiste() {
        when(reservaRepository.existsById(TEST_ID_NOT_FOUND)).thenReturn(false);
        ReservaRequestDTO dto = new ReservaRequestDTO(1L, "Juan", LocalDateTime.now().plusDays(1), 4);
        assertThrows(ReservaNotFoundException.class, () -> reservaService.actualizar(TEST_ID_NOT_FOUND, dto));
        verify(reservaRepository, never()).save(any());
    }

    private void mockSecurityContext() {
        org.springframework.security.core.Authentication auth = mock(org.springframework.security.core.Authentication.class);
        lenient().when(auth.getName()).thenReturn("admin");
        lenient().when(auth.getAuthorities()).thenReturn((java.util.Collection) java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN")));
        org.springframework.security.core.context.SecurityContext context = mock(org.springframework.security.core.context.SecurityContext.class);
        lenient().when(context.getAuthentication()).thenReturn(auth);
        org.springframework.security.core.context.SecurityContextHolder.setContext(context);
    }
}
