package com.restaurante.service;

import com.restaurante.exception.CuentaNotFoundException;
import com.restaurante.exception.MesaNotFoundException;
import com.restaurante.model.dto.request.PagoCuentaRequestDTO;
import com.restaurante.persistence.entity.CuentaEntity;
import com.restaurante.model.domain.EstadoCuenta;
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
class CuentaServiceImplTest {
    @Mock private CuentaRepositoryJPA cuentaRepository;
    @Mock private MesaRepositoryJPA mesaRepository;
    @Spy private CuentaEntityMapper cuentaEntityMapper = new CuentaEntityMapper(new ItemPedidoEntityMapper());
    @Spy private MesaEntityMapper mesaEntityMapper = new MesaEntityMapper();
    @Mock private CuentaMapperOut cuentaMapperOut;
    @InjectMocks private CuentaServiceImpl cuentaService;

    @Test
    void init() { assertNotNull(cuentaService); }

    @Test
    void obtenerCuentaActivaPorMesa_lanzaExcepcionSiNoExiste() {
        when(cuentaRepository.findAll()).thenReturn(List.of());
        assertThrows(CuentaNotFoundException.class, () -> cuentaService.obtenerCuentaActivaPorMesa(99L));
    }

    @Test
    void registrarPago_lanzaExcepcionSiMesaNoExiste() {
        CuentaEntity cuentaEntity = new CuentaEntity();
        cuentaEntity.setIdMesa(1L);
        cuentaEntity.setEstado(EstadoCuenta.ABIERTA);
        
        when(cuentaRepository.findAll()).thenReturn(List.of(cuentaEntity));
        when(mesaRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(MesaNotFoundException.class, () -> cuentaService.registrarPago(1L, new PagoCuentaRequestDTO("EFECTIVO", 50000.0)));
    }
}
