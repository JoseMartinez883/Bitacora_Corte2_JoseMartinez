package com.restaurante.service;

import com.restaurante.exception.MesaNoDisponibleException;
import com.restaurante.exception.MesaNotFoundException;
import com.restaurante.persistence.entity.MesaEntity;
import com.restaurante.model.domain.EstadoMesa;
import java.util.Optional;
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
class MesaServiceImplTest {
    @Mock private MesaRepositoryJPA mesaRepository;
    @Spy private MesaEntityMapper mesaEntityMapper = new MesaEntityMapper();
    @Mock private MesaMapperOut mesaMapperOut;
    @InjectMocks private MesaServiceImpl mesaService;

    @Test
    void init() { assertNotNull(mesaService); }

    @Test
    void obtenerMesaPorId_lanzaExcepcionSiNoExiste() {
        when(mesaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(MesaNotFoundException.class, () -> mesaService.obtenerMesaPorId(99L));
    }

    @Test
    void abrirCuenta_lanzaExcepcionSiNoExiste() {
        when(mesaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(MesaNotFoundException.class, () -> mesaService.abrirCuenta(99L));
    }

    @Test
    void abrirCuenta_lanzaExcepcionSiMesaOcupada() {
        MesaEntity mesaEntity = new MesaEntity();
        mesaEntity.setId(1L);
        mesaEntity.setEstado(EstadoMesa.OCUPADA);
        mesaEntity.setCuentaAbierta(true);
        when(mesaRepository.findById(1L)).thenReturn(Optional.of(mesaEntity));
        
        assertThrows(MesaNoDisponibleException.class, () -> mesaService.abrirCuenta(1L));
        verify(mesaRepository, never()).save(any());
    }
}
