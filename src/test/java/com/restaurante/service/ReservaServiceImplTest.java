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
    @Mock private ReservaMapperIn reservaMapperIn;
    @Mock private ReservaMapperOut reservaMapperOut;
    @InjectMocks private ReservaServiceImpl reservaService;

    @Test
    void init() { assertNotNull(reservaService); }

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
