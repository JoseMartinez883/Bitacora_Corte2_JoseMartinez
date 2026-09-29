package com.restaurante.service;

import com.restaurante.exception.MesaNotFoundException;
import com.restaurante.exception.MesaNoDisponibleException;
import com.restaurante.mapper.MesaMapperOut;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.dto.response.MesaResponseDTO;
import com.restaurante.repository.MesaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MesaServiceImplTest {

    @Mock private MesaRepository mesaRepository;
    @Mock private MesaMapperOut mesaMapperOut;
    @InjectMocks private MesaServiceImpl mesaService;

    private Mesa mesaDisponibleMock;
    private Mesa mesaOcupadaMock;

    @BeforeEach
    void setUp() {
        mesaDisponibleMock = new Mesa();
        mesaDisponibleMock.setId(1L);
        mesaDisponibleMock.setNumero(1);
        mesaDisponibleMock.setEstado(EstadoMesa.DISPONIBLE);
        mesaDisponibleMock.setCuentaAbierta(false);

        mesaOcupadaMock = new Mesa();
        mesaOcupadaMock.setId(2L);
        mesaOcupadaMock.setNumero(2);
        mesaOcupadaMock.setEstado(EstadoMesa.OCUPADA);
        mesaOcupadaMock.setCuentaAbierta(true); // Ya tiene cuenta abierta
    }

    @Test
    @DisplayName("1. Happy Path: obtenerMesaPorId retorna mesa correctamente")
    void obtenerMesaPorId_Existente_RetornaMesa() {
        MesaResponseDTO responseMock = mock(MesaResponseDTO.class);
        when(mesaRepository.buscarPorId(1L)).thenReturn(Optional.of(mesaDisponibleMock));
        when(mesaMapperOut.toResponse(mesaDisponibleMock)).thenReturn(responseMock);

        MesaResponseDTO resultado = mesaService.obtenerMesaPorId(1L);

        assertNotNull(resultado);
        verify(mesaRepository).buscarPorId(1L);
    }

    @Test
    @DisplayName("2. No encontrado: obtenerMesaPorId con ID inexistente")
    void obtenerMesaPorId_Inexistente_LanzaMesaNotFoundException() {
        when(mesaRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(MesaNotFoundException.class,
                () -> mesaService.obtenerMesaPorId(99L));
    }

    @Test
    @DisplayName("3. Conflicto: abrirCuenta en mesa que ya tiene cuenta abierta")
    void abrirCuenta_MesaOcupada_LanzaMesaNoDisponibleException() {
        // Mesa en estado OCUPADA con cuenta abierta → Mesa.abrirCuenta() lanza excepción
        when(mesaRepository.buscarPorId(2L)).thenReturn(Optional.of(mesaOcupadaMock));

        assertThrows(MesaNoDisponibleException.class,
                () -> mesaService.abrirCuenta(2L));
    }

    @Test
    @DisplayName("4. Estado inválido: abrirCuenta en mesa inexistente")
    void abrirCuenta_MesaNoExistente_LanzaMesaNotFoundException() {
        // El service busca la mesa y lanza excepción antes de llamar abrirCuenta()
        when(mesaRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(MesaNotFoundException.class,
                () -> mesaService.abrirCuenta(99L));
    }

    @Test
    @DisplayName("5. Lista vacía: listarTodas retorna lista vacía, no null")
    void listarTodas_SinMesas_RetornaListaVacia() {
        when(mesaRepository.buscarTodas()).thenReturn(Collections.emptyList());

        List<MesaResponseDTO> resultado = mesaService.listarTodas();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}

