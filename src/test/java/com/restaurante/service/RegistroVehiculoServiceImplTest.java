package com.restaurante.service;

import com.restaurante.exception.VehiculoNotFoundException;
import com.restaurante.mapper.RegistroVehiculoMapperIn;
import com.restaurante.mapper.RegistroVehiculoMapperOut;
import com.restaurante.model.domain.RegistroVehiculo;
import com.restaurante.model.dto.response.RegistroVehiculoResponseDTO;
import com.restaurante.repository.RegistroVehiculoRepository;
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
class RegistroVehiculoServiceImplTest {

    @Mock private RegistroVehiculoRepository registroRepository;
    @Mock private RegistroVehiculoMapperIn mapperIn;
    @Mock private RegistroVehiculoMapperOut mapperOut;
    @InjectMocks private RegistroVehiculoServiceImpl vehiculoService;

    private RegistroVehiculo registroActivoMock;
    private RegistroVehiculo registroSalidaMock;

    @BeforeEach
    void setUp() {
        registroActivoMock = new RegistroVehiculo();
        registroActivoMock.setId(1L);
        registroActivoMock.setPlaca("ABC-123");
        registroActivoMock.setEntrada(LocalDateTime.now().minusHours(2));
        registroActivoMock.setEstado("ACTIVO");

        registroSalidaMock = new RegistroVehiculo();
        registroSalidaMock.setId(2L);
        registroSalidaMock.setPlaca("XYZ-999");
        registroSalidaMock.setEntrada(LocalDateTime.now().minusHours(1));
        registroSalidaMock.setSalida(LocalDateTime.now()); // Ya tiene salida
        registroSalidaMock.setEstado("FINALIZADO");
    }

    @Test
    @DisplayName("1. Happy Path: listarActivos retorna lista con vehículos")
    void listarActivos_ConVehiculos_RetornaLista() {
        RegistroVehiculoResponseDTO responseMock = mock(RegistroVehiculoResponseDTO.class);
        when(registroRepository.buscarActivos()).thenReturn(List.of(registroActivoMock));
        when(mapperOut.toResponse(registroActivoMock)).thenReturn(responseMock);

        List<RegistroVehiculoResponseDTO> resultado = vehiculoService.listarActivos();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("2. No encontrado: registrarSalida con ID inexistente")
    void registrarSalida_IdInexistente_LanzaVehiculoNotFoundException() {
        when(registroRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(VehiculoNotFoundException.class,
                () -> vehiculoService.registrarSalida(99L));
    }

    @Test
    @DisplayName("3. Conflicto: registrarSalida flujo exitoso calcula el cobro")
    void registrarSalida_Exitoso_CalculaCobroYGuarda() {
        RegistroVehiculoResponseDTO responseMock = mock(RegistroVehiculoResponseDTO.class);
        when(registroRepository.buscarPorId(1L)).thenReturn(Optional.of(registroActivoMock));
        when(registroRepository.guardar(registroActivoMock)).thenReturn(registroActivoMock);
        when(mapperOut.toResponse(registroActivoMock)).thenReturn(responseMock);

        RegistroVehiculoResponseDTO resultado = vehiculoService.registrarSalida(1L);

        assertNotNull(resultado);
        assertEquals("FINALIZADO", registroActivoMock.getEstado());
        assertNotNull(registroActivoMock.getSalida());
    }

    @Test
    @DisplayName("4. Estado inválido: registrarSalida de vehículo ya finalizado")
    void registrarSalida_YaFinalizado_AunAsiFunciona() {
        // RegistroVehiculo.registrarSalida() sobreescribe la salida aunque ya tenga una.
        // Probamos que el servicio procesa sin lanzar excepción inesperada.
        RegistroVehiculoResponseDTO responseMock = mock(RegistroVehiculoResponseDTO.class);
        when(registroRepository.buscarPorId(2L)).thenReturn(Optional.of(registroSalidaMock));
        when(registroRepository.guardar(registroSalidaMock)).thenReturn(registroSalidaMock);
        when(mapperOut.toResponse(registroSalidaMock)).thenReturn(responseMock);

        assertDoesNotThrow(() -> vehiculoService.registrarSalida(2L));
    }

    @Test
    @DisplayName("5. Lista vacía: listarActivos retorna lista vacía, no null")
    void listarActivos_SinVehiculos_RetornaListaVacia() {
        when(registroRepository.buscarActivos()).thenReturn(Collections.emptyList());

        List<RegistroVehiculoResponseDTO> resultado = vehiculoService.listarActivos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}
