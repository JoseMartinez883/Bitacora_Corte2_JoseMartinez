package com.restaurante.service;

import com.restaurante.exception.ReservaNotFoundException;
import com.restaurante.model.dto.request.ReservaRequestDTO;
import java.time.LocalDateTime;
import java.util.Optional;
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
    @Spy private ReservaEntityMapper entityMapper = new ReservaEntityMapper();
    @Spy private ReservaMapperIn reservaMapperIn = new ReservaMapperIn();
    @Spy private ReservaMapperOut reservaMapperOut = new ReservaMapperOut();
    @InjectMocks private ReservaServiceImpl reservaService;

    @Test
    void init() { assertNotNull(reservaService); }

    @Test
    void crearReserva_exitoso() {
        com.restaurante.persistence.entity.ReservaEntity entity = new com.restaurante.persistence.entity.ReservaEntity();
        entity.setId(1L);
        entity.setCliente("Juan");
        entity.setActiva(true);
        when(reservaRepository.save(any())).thenReturn(entity);
        
        ReservaRequestDTO dto = new ReservaRequestDTO(1L, "Juan", LocalDateTime.now().plusDays(1), 4);
        assertNotNull(reservaService.crearReserva(dto));
    }

    @Test
    void obtenerReservaPorId_exitoso() {
        com.restaurante.persistence.entity.ReservaEntity entity = new com.restaurante.persistence.entity.ReservaEntity();
        entity.setId(1L);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(entity));
        assertNotNull(reservaService.obtenerReservaPorId(1L));
    }

    @Test
    void actualizar_exitoso() {
        com.restaurante.persistence.entity.ReservaEntity entity = new com.restaurante.persistence.entity.ReservaEntity();
        entity.setId(1L);
        when(reservaRepository.existsById(1L)).thenReturn(true);
        when(reservaRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        
        ReservaRequestDTO dto = new ReservaRequestDTO(1L, "Juan", LocalDateTime.now().plusDays(1), 4);
        assertNotNull(reservaService.actualizar(1L, dto));
    }

    @Test
    void cancelarReserva_exitoso() {
        com.restaurante.persistence.entity.ReservaEntity entity = new com.restaurante.persistence.entity.ReservaEntity();
        entity.setId(1L);
        entity.setActiva(true);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(reservaRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        
        var response = reservaService.cancelarReserva(1L);
        assertFalse(response.activa());
        verify(reservaRepository).save(any());
    }

    @Test
    void obtenerReservaPorId_lanzaExcepcionSiNoExiste() {
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ReservaNotFoundException.class, () -> reservaService.obtenerReservaPorId(99L));
    }

    @Test
    void cancelarReserva_lanzaExcepcionSiNoExiste() {
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ReservaNotFoundException.class, () -> reservaService.cancelarReserva(99L));
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void actualizar_lanzaExcepcionSiNoExiste() {
        when(reservaRepository.existsById(99L)).thenReturn(false);
        ReservaRequestDTO dto = new ReservaRequestDTO(1L, "Juan", LocalDateTime.now().plusDays(1), 4);
        assertThrows(ReservaNotFoundException.class, () -> reservaService.actualizar(99L, dto));
        verify(reservaRepository, never()).save(any());
    }
}
