package com.restaurante.service;

import com.restaurante.exception.ReservaNotFoundException;
import com.restaurante.mapper.ReservaMapperIn;
import com.restaurante.mapper.ReservaMapperOut;
import com.restaurante.model.domain.Reserva;
import com.restaurante.model.dto.response.ReservaResponseDTO;
import com.restaurante.repository.ReservaRepository;
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
class ReservaServiceImplTest {

    @Mock private ReservaRepository reservaRepository;
    @Mock private ReservaMapperIn reservaMapperIn;
    @Mock private ReservaMapperOut reservaMapperOut;
    @InjectMocks private ReservaServiceImpl reservaService;

    private Reserva reservaActivaMock;
    private Reserva reservaCanceladaMock;

    @BeforeEach
    void setUp() {
        reservaActivaMock = new Reserva();
        reservaActivaMock.setId(1L);
        reservaActivaMock.setActiva(true);
        reservaActivaMock.setFechaHora(LocalDateTime.now().plusDays(1));

        reservaCanceladaMock = new Reserva();
        reservaCanceladaMock.setId(2L);
        reservaCanceladaMock.setActiva(false);
    }

    @Test
    @DisplayName("1. Happy Path: obtenerReservaPorId retorna la reserva correctamente")
    void obtenerReservaPorId_Existente_RetornaReserva() {
        ReservaResponseDTO responseMock = mock(ReservaResponseDTO.class);
        when(reservaRepository.buscarPorId(1L)).thenReturn(Optional.of(reservaActivaMock));
        when(reservaMapperOut.toResponse(reservaActivaMock)).thenReturn(responseMock);

        ReservaResponseDTO resultado = reservaService.obtenerReservaPorId(1L);

        assertNotNull(resultado);
        verify(reservaRepository).buscarPorId(1L);
    }

    @Test
    @DisplayName("2. No encontrado: obtenerReservaPorId con ID inexistente")
    void obtenerReservaPorId_Inexistente_LanzaReservaNotFoundException() {
        when(reservaRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(ReservaNotFoundException.class,
                () -> reservaService.obtenerReservaPorId(99L));
    }

    @Test
    @DisplayName("3. Conflicto: cancelarReserva con ID inexistente lanza excepción")
    void cancelarReserva_NoExistente_LanzaReservaNotFoundException() {
        when(reservaRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(ReservaNotFoundException.class,
                () -> reservaService.cancelarReserva(99L));
    }

    @Test
    @DisplayName("4. Estado inválido: cancelar reserva ya cancelada es idempotente")
    void cancelarReserva_YaCancelada_NoLanzaExcepcion() {
        // Reserva.cancelar() solo hace activa=false, no lanza excepción.
        when(reservaRepository.buscarPorId(2L)).thenReturn(Optional.of(reservaCanceladaMock));
        when(reservaRepository.guardar(reservaCanceladaMock)).thenReturn(reservaCanceladaMock);
        when(reservaMapperOut.toResponse(reservaCanceladaMock)).thenReturn(mock(ReservaResponseDTO.class));

        assertDoesNotThrow(() -> reservaService.cancelarReserva(2L));
        assertFalse(reservaCanceladaMock.isActiva());
    }

    @Test
    @DisplayName("5. Lista vacía: listarTodas retorna lista vacía, no null")
    void listarTodas_SinReservas_RetornaListaVacia() {
        when(reservaRepository.buscarTodas()).thenReturn(Collections.emptyList());

        List<ReservaResponseDTO> resultado = reservaService.listarTodas();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}
