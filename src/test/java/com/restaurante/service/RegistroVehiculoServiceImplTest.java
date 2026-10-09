package com.restaurante.service;

import java.util.List;

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
class RegistroVehiculoServiceImplTest {
    @Mock private RegistroVehiculoRepositoryJPA registroRepository;
    @Spy private RegistroVehiculoEntityMapper entityMapper = new RegistroVehiculoEntityMapperImpl();
    @Spy private RegistroVehiculoMapperIn mapperIn = new RegistroVehiculoMapperInImpl();
    @Spy private RegistroVehiculoMapperOut mapperOut = new RegistroVehiculoMapperOutImpl();
    @InjectMocks private RegistroVehiculoServiceImpl vehiculoService;

    @Test
    void init() { assertNotNull(vehiculoService); }

    @Test
    void registrarEntrada_exitoso() {
        com.restaurante.persistence.entity.RegistroVehiculoEntity entity = new com.restaurante.persistence.entity.RegistroVehiculoEntity();
        entity.setId(1L);
        entity.setPlaca("ABC-123");
        when(registroRepository.save(any())).thenReturn(entity);
        
        com.restaurante.model.dto.request.RegistroVehiculoRequestDTO dto = new com.restaurante.model.dto.request.RegistroVehiculoRequestDTO("ABC-123");
        assertNotNull(vehiculoService.registrarEntrada(dto));
    }

    @Test
    void registrarSalida_exitoso() {
        com.restaurante.persistence.entity.RegistroVehiculoEntity entity = new com.restaurante.persistence.entity.RegistroVehiculoEntity();
        entity.setId(1L);
        entity.setPlaca("ABC-123");
        entity.setEntrada(java.time.LocalDateTime.now().minusHours(2).minusMinutes(30)); // 2h 30m = 3 hours billed
        when(registroRepository.findById(1L)).thenReturn(java.util.Optional.of(entity));
        when(registroRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        
        var response = vehiculoService.registrarSalida(1L);
        assertNotNull(response.salida());
        assertEquals(9000.0, response.cobro()); // 3 hours * 3000
        verify(registroRepository).save(any());
    }

    @Test
    void listarActivos_exitoso() {
        var entity = new com.restaurante.persistence.entity.RegistroVehiculoEntity();
        entity.setEstado("ACTIVO");
        when(registroRepository.findAll()).thenReturn(List.of(entity));
        assertFalse(vehiculoService.listarActivos().isEmpty());
    }

    @Test
    void listarTodos_exitoso() {
        when(registroRepository.findAll()).thenReturn(List.of(new com.restaurante.persistence.entity.RegistroVehiculoEntity()));
        assertFalse(vehiculoService.listarTodos().isEmpty());
    }

    @Test
    void registrarEntrada_lanzaExcepcionSiParqueaderoLleno() {
        java.util.List<com.restaurante.persistence.entity.RegistroVehiculoEntity> ocupados = new java.util.ArrayList<>();
        for (int i = 0; i < 20; i++) {
            var v = new com.restaurante.persistence.entity.RegistroVehiculoEntity();
            v.setEstado("ACTIVO");
            v.setSalida(null);
            ocupados.add(v);
        }
        when(registroRepository.findAll()).thenReturn(ocupados);

        var dto = new com.restaurante.model.dto.request.RegistroVehiculoRequestDTO("NEW-999");
        assertThrows(com.restaurante.exception.ParqueaderoLlenoException.class, () -> vehiculoService.registrarEntrada(dto));
    }

    @Test
    void registrarEntrada_lanzaExcepcionSiPlacaYaEstacionada() {
        var activo = new com.restaurante.persistence.entity.RegistroVehiculoEntity();
        activo.setPlaca("DUP-123");
        activo.setEstado("ACTIVO");
        activo.setSalida(null);
        when(registroRepository.findAll()).thenReturn(List.of(activo));

        var dto = new com.restaurante.model.dto.request.RegistroVehiculoRequestDTO("DUP-123");
        assertThrows(com.restaurante.exception.VehiculoYaEstacionadoException.class, () -> vehiculoService.registrarEntrada(dto));
    }

    @Test
    void registrarSalidaPorPlaca_exitoso() {
        var entity = new com.restaurante.persistence.entity.RegistroVehiculoEntity();
        entity.setId(10L);
        entity.setPlaca("OUT-456");
        entity.setEstado("ACTIVO");
        entity.setEntrada(java.time.LocalDateTime.now().minusHours(1));
        when(registroRepository.findAll()).thenReturn(List.of(entity));
        when(registroRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var response = vehiculoService.registrarSalidaPorPlaca("OUT-456");
        assertNotNull(response);
        assertEquals("OUT-456", response.placa());
        verify(registroRepository).save(any());
    }
}
