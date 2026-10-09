package com.restaurante.service;

import com.restaurante.exception.ReservaNotFoundException;
import com.restaurante.model.dto.request.ReservaRequestDTO;
import java.time.LocalDateTime;
import java.util.List;
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
    
    private final UUID testId = UUID.randomUUID();
    private final UUID testIdNotFound = UUID.randomUUID();

    @Test
    void init() { assertNotNull(reservaService); }

    @Test
    void crearReserva_exitoso() {
        mockSecurityContext();
        com.restaurante.persistence.entity.ReservaEntity entity = new com.restaurante.persistence.entity.ReservaEntity();
        entity.setId(testId);
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
        entity.setId(testId);
        when(reservaRepository.findById(testId)).thenReturn(Optional.of(entity));
        assertNotNull(reservaService.obtenerReservaPorId(testId));
    }

    @Test
    void actualizar_exitoso() {
        mockSecurityContext();
        com.restaurante.persistence.entity.ReservaEntity entity = new com.restaurante.persistence.entity.ReservaEntity();
        entity.setId(testId);
        when(reservaRepository.existsById(testId)).thenReturn(true);
        when(reservaRepository.findById(testId)).thenReturn(Optional.of(entity));
        when(reservaRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        
        ReservaRequestDTO dto = new ReservaRequestDTO(1L, "Juan", LocalDateTime.now().plusDays(1), 4);
        assertNotNull(reservaService.actualizar(testId, dto));
    }

    @Test
    void cancelarReserva_exitoso() {
        mockSecurityContext();
        com.restaurante.persistence.entity.ReservaEntity entity = new com.restaurante.persistence.entity.ReservaEntity();
        entity.setId(testId);
        entity.setActiva(true);
        when(reservaRepository.findById(testId)).thenReturn(Optional.of(entity));
        when(reservaRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        
        var response = reservaService.cancelarReserva(testId);
        assertFalse(response.activa());
        verify(reservaRepository).save(any());
    }

    @Test
    void obtenerReservaPorId_lanzaExcepcionSiNoExiste() {
        when(reservaRepository.findById(testIdNotFound)).thenReturn(Optional.empty());
        assertThrows(ReservaNotFoundException.class, () -> reservaService.obtenerReservaPorId(testIdNotFound));
    }

    @Test
    void cancelarReserva_lanzaExcepcionSiNoExiste() {
        when(reservaRepository.findById(testIdNotFound)).thenReturn(Optional.empty());
        assertThrows(ReservaNotFoundException.class, () -> reservaService.cancelarReserva(testIdNotFound));
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void actualizar_lanzaExcepcionSiNoExiste() {
        when(reservaRepository.existsById(testIdNotFound)).thenReturn(false);
        ReservaRequestDTO dto = new ReservaRequestDTO(1L, "Juan", LocalDateTime.now().plusDays(1), 4);
        assertThrows(ReservaNotFoundException.class, () -> reservaService.actualizar(testIdNotFound, dto));
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void crearReserva_lanzaExcepcionSiMesaYaEstaReservadaEnHorario() {
        mockSecurityContext();
        LocalDateTime fecha = LocalDateTime.now().plusDays(1).withHour(20).withMinute(0);
        var existente = new com.restaurante.persistence.entity.ReservaEntity();
        existente.setId(UUID.randomUUID());
        existente.setIdMesa(1L);
        existente.setActiva(true);
        existente.setFechaHora(fecha);
        when(reservaRepository.findAll()).thenReturn(List.of(existente));

        ReservaRequestDTO dto = new ReservaRequestDTO(1L, "Pedro", fecha.plusMinutes(30), 2);
        assertThrows(com.restaurante.exception.MesaYaReservadaException.class, () -> reservaService.crearReserva(dto));
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void crearReserva_coberturaDeFiltros() {
        mockSecurityContext();
        LocalDateTime fecha = LocalDateTime.now().plusDays(1).withHour(20).withMinute(0);
        
        var inactiva = new com.restaurante.persistence.entity.ReservaEntity();
        inactiva.setActiva(false);
        inactiva.setIdMesa(1L);
        
        var mesaNull = new com.restaurante.persistence.entity.ReservaEntity();
        mesaNull.setActiva(true);
        mesaNull.setIdMesa(null);
        
        var otraMesa = new com.restaurante.persistence.entity.ReservaEntity();
        otraMesa.setActiva(true);
        otraMesa.setIdMesa(2L);
        
        var sinFecha = new com.restaurante.persistence.entity.ReservaEntity();
        sinFecha.setActiva(true);
        sinFecha.setIdMesa(1L);
        sinFecha.setFechaHora(null);
        
        var fueraDeHorario = new com.restaurante.persistence.entity.ReservaEntity();
        fueraDeHorario.setActiva(true);
        fueraDeHorario.setIdMesa(1L);
        fueraDeHorario.setFechaHora(fecha.minusHours(3)); // 180 min diff

        when(reservaRepository.findAll()).thenReturn(List.of(inactiva, mesaNull, otraMesa, sinFecha, fueraDeHorario));

        ReservaRequestDTO dto = new ReservaRequestDTO(1L, "Pedro", fecha, 2);
        when(reservaRepository.save(any())).thenReturn(inactiva);
        assertNotNull(reservaService.crearReserva(dto));
    }

    @Test
    void actualizar_coberturaDeFiltrosConflicto() {
        mockSecurityContext();
        com.restaurante.persistence.entity.ReservaEntity entity = new com.restaurante.persistence.entity.ReservaEntity();
        entity.setId(testId);
        entity.setUsuarioId(12345L);
        when(reservaRepository.existsById(testId)).thenReturn(true);
        when(reservaRepository.findById(testId)).thenReturn(Optional.of(entity));
        
        LocalDateTime fecha = LocalDateTime.now().plusDays(1).withHour(20).withMinute(0);
        
        var mismaReserva = new com.restaurante.persistence.entity.ReservaEntity();
        mismaReserva.setId(testId); // Mismo ID
        mismaReserva.setActiva(true);
        mismaReserva.setIdMesa(1L);
        mismaReserva.setFechaHora(fecha);
        
        var conflicto = new com.restaurante.persistence.entity.ReservaEntity();
        conflicto.setId(UUID.randomUUID());
        conflicto.setActiva(true);
        conflicto.setIdMesa(1L);
        conflicto.setFechaHora(fecha);
        
        when(reservaRepository.findAll()).thenReturn(List.of(mismaReserva, conflicto));
        
        ReservaRequestDTO dto = new ReservaRequestDTO(1L, "Juan", fecha, 4);
        assertThrows(com.restaurante.exception.MesaYaReservadaException.class, () -> reservaService.actualizar(testId, dto));
    }

    @Test
    void obtenerReservaPorId_fallaPermisosCliente() {
        mockSecurityContextCliente("cliente@test.com");
        com.restaurante.persistence.entity.ReservaEntity entity = new com.restaurante.persistence.entity.ReservaEntity();
        entity.setId(testId);
        entity.setUsuarioId(12345L); // Un hash diferente
        when(reservaRepository.findById(testId)).thenReturn(Optional.of(entity));
        assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> reservaService.obtenerReservaPorId(testId));
    }

    @Test
    void listarTodas_exitoso() {
        when(reservaRepository.findAll()).thenReturn(List.of(new com.restaurante.persistence.entity.ReservaEntity()));
        var lista = reservaService.listarTodas();
        assertFalse(lista.isEmpty());
    }

    private void mockSecurityContextCliente(String email) {
        org.springframework.security.core.Authentication auth = mock(org.springframework.security.core.Authentication.class);
        lenient().when(auth.getName()).thenReturn(email);
        lenient().when(auth.getAuthorities()).thenReturn((java.util.Collection) java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_CLIENTE")));
        org.springframework.security.core.context.SecurityContext context = mock(org.springframework.security.core.context.SecurityContext.class);
        lenient().when(context.getAuthentication()).thenReturn(auth);
        org.springframework.security.core.context.SecurityContextHolder.setContext(context);
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
