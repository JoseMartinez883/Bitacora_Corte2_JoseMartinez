package com.restaurante.service;

import com.restaurante.exception.CuentaNotFoundException;
import com.restaurante.mapper.CuentaMapperOut;
import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.domain.EstadoCuenta;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.dto.request.PagoCuentaRequestDTO;
import com.restaurante.model.dto.response.CuentaResponseDTO;
import com.restaurante.repository.CuentaRepository;
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
class CuentaServiceImplTest {

    @Mock private CuentaRepository cuentaRepository;
    @Mock private MesaRepository mesaRepository;
    @Mock private CuentaMapperOut cuentaMapperOut;
    @InjectMocks private CuentaServiceImpl cuentaService;

    private Cuenta cuentaAbiertaMock;
    private Mesa mesaMock;

    @BeforeEach
    void setUp() {
        cuentaAbiertaMock = new Cuenta();
        cuentaAbiertaMock.setId(1L);
        cuentaAbiertaMock.setIdMesa(1L);
        cuentaAbiertaMock.setEstado(EstadoCuenta.ABIERTA);

        mesaMock = new Mesa();
        mesaMock.setId(1L);
        mesaMock.setEstado(EstadoMesa.OCUPADA);
        mesaMock.setCuentaAbierta(true);
    }

    @Test
    @DisplayName("1. Happy Path: obtenerCuentaActivaPorMesa retorna la cuenta")
    void obtenerCuentaActivaPorMesa_Existente_RetornaCuenta() {
        CuentaResponseDTO responseMock = mock(CuentaResponseDTO.class);
        when(cuentaRepository.buscarCuentaActivaPorMesa(1L)).thenReturn(Optional.of(cuentaAbiertaMock));
        when(cuentaMapperOut.toResponse(cuentaAbiertaMock)).thenReturn(responseMock);

        CuentaResponseDTO resultado = cuentaService.obtenerCuentaActivaPorMesa(1L);

        assertNotNull(resultado);
        verify(cuentaRepository).buscarCuentaActivaPorMesa(1L);
    }

    @Test
    @DisplayName("2. No encontrado: obtenerCuentaActivaPorMesa sin cuenta activa")
    void obtenerCuentaActivaPorMesa_NoExiste_LanzaCuentaNotFoundException() {
        when(cuentaRepository.buscarCuentaActivaPorMesa(99L)).thenReturn(Optional.empty());

        assertThrows(CuentaNotFoundException.class,
                () -> cuentaService.obtenerCuentaActivaPorMesa(99L));
    }

    @Test
    @DisplayName("3. Conflicto: registrarPago en mesa sin cuenta activa")
    void registrarPago_SinCuentaActiva_LanzaCuentaNotFoundException() {
        PagoCuentaRequestDTO dto = new PagoCuentaRequestDTO("EFECTIVO", 50000.0);
        when(cuentaRepository.buscarCuentaActivaPorMesa(99L)).thenReturn(Optional.empty());

        assertThrows(CuentaNotFoundException.class,
                () -> cuentaService.registrarPago(99L, dto));
    }

    @Test
    @DisplayName("4. Estado inválido: registrarPago flujo completo cierra cuenta y mesa")
    void registrarPago_CuentaAbierta_CierraCuentaYMesa() {
        PagoCuentaRequestDTO dto = new PagoCuentaRequestDTO("TARJETA", 50000.0);
        when(cuentaRepository.buscarCuentaActivaPorMesa(1L)).thenReturn(Optional.of(cuentaAbiertaMock));
        when(mesaRepository.buscarPorId(1L)).thenReturn(Optional.of(mesaMock));
        when(cuentaRepository.guardar(cuentaAbiertaMock)).thenReturn(cuentaAbiertaMock);
        when(mesaRepository.guardar(mesaMock)).thenReturn(mesaMock);
        when(cuentaMapperOut.toResponse(cuentaAbiertaMock)).thenReturn(mock(CuentaResponseDTO.class));

        cuentaService.registrarPago(1L, dto);

        assertEquals(EstadoCuenta.CERRADA, cuentaAbiertaMock.getEstado());
        assertFalse(mesaMock.isCuentaAbierta());
    }

    @Test
    @DisplayName("5. Lista vacía: buscarTodas retorna vacío correctamente desde repo")
    void buscarTodas_SinCuentas_RetornaListaVacia() {
        when(cuentaRepository.buscarTodas()).thenReturn(Collections.emptyList());

        List<Cuenta> resultado = cuentaRepository.buscarTodas();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}

