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
    @Mock private PedidoRepositoryJPA pedidoRepository;
    @Spy private CuentaEntityMapper cuentaEntityMapper = new CuentaEntityMapperImpl(new ItemPedidoEntityMapperImpl());
    @Spy private MesaEntityMapper mesaEntityMapper = new MesaEntityMapperImpl();
    @Spy private CuentaMapperOut cuentaMapperOut = new CuentaMapperOutImpl();
    @InjectMocks private CuentaServiceImpl cuentaService;

    @Test
    void init() { assertNotNull(cuentaService); }

    @Test
    void obtenerCuentaActivaPorMesa_exitoso() {
        CuentaEntity cuentaEntity = new CuentaEntity();
        cuentaEntity.setIdMesa(1L);
        cuentaEntity.setEstado(EstadoCuenta.ABIERTA);
        cuentaEntity.setItems(List.of());
        when(cuentaRepository.findByIdMesaAndEstado(1L, EstadoCuenta.ABIERTA)).thenReturn(Optional.of(cuentaEntity));
        
        assertNotNull(cuentaService.obtenerCuentaActivaPorMesa(1L));
    }

    @Test
    void registrarPago_exitoso() {
        CuentaEntity cuentaEntity = new CuentaEntity();
        cuentaEntity.setIdMesa(1L);
        cuentaEntity.setEstado(EstadoCuenta.ABIERTA);
        cuentaEntity.setItems(List.of());
        
        com.restaurante.persistence.entity.MesaEntity mesaEntity = new com.restaurante.persistence.entity.MesaEntity();
        mesaEntity.setId(1L);
        mesaEntity.setEstado(com.restaurante.model.domain.EstadoMesa.OCUPADA);
        
        when(cuentaRepository.findByIdMesaAndEstado(1L, EstadoCuenta.ABIERTA)).thenReturn(Optional.of(cuentaEntity));
        when(pedidoRepository.existsByIdMesaAndEstadoNot(1L, com.restaurante.model.domain.EstadoPedido.ENTREGADO)).thenReturn(false);
        when(mesaRepository.findById(1L)).thenReturn(Optional.of(mesaEntity));
        when(cuentaRepository.save(any())).thenReturn(cuentaEntity);
        
        assertNotNull(cuentaService.registrarPago(1L, new PagoCuentaRequestDTO("EFECTIVO", 50000.0)));
    }

    @Test
    void obtenerCuentaActivaPorMesa_lanzaExcepcionSiNoExiste() {
        when(cuentaRepository.findByIdMesaAndEstado(99L, EstadoCuenta.ABIERTA)).thenReturn(Optional.empty());
        assertThrows(CuentaNotFoundException.class, () -> cuentaService.obtenerCuentaActivaPorMesa(99L));
    }

    @Test
    void registrarPago_lanzaExcepcionSiMesaNoExiste() {
        CuentaEntity cuentaEntity = new CuentaEntity();
        cuentaEntity.setIdMesa(1L);
        cuentaEntity.setEstado(EstadoCuenta.ABIERTA);
        
        when(cuentaRepository.findByIdMesaAndEstado(1L, EstadoCuenta.ABIERTA)).thenReturn(Optional.of(cuentaEntity));
        when(pedidoRepository.existsByIdMesaAndEstadoNot(1L, com.restaurante.model.domain.EstadoPedido.ENTREGADO)).thenReturn(false);
        when(mesaRepository.findById(1L)).thenReturn(Optional.empty());
        
        PagoCuentaRequestDTO req = new PagoCuentaRequestDTO("EFECTIVO", 50000.0);
        assertThrows(MesaNotFoundException.class, () -> cuentaService.registrarPago(1L, req));
    }

    @Test
    void registrarPago_lanzaExcepcionSiHayPedidosPendientes() {
        CuentaEntity cuentaEntity = new CuentaEntity();
        cuentaEntity.setIdMesa(1L);
        cuentaEntity.setEstado(EstadoCuenta.ABIERTA);

        when(cuentaRepository.findByIdMesaAndEstado(1L, EstadoCuenta.ABIERTA)).thenReturn(Optional.of(cuentaEntity));
        when(pedidoRepository.existsByIdMesaAndEstadoNot(1L, com.restaurante.model.domain.EstadoPedido.ENTREGADO)).thenReturn(true);

        PagoCuentaRequestDTO req = new PagoCuentaRequestDTO("EFECTIVO", 50000.0);
        assertThrows(IllegalStateException.class, () -> cuentaService.registrarPago(1L, req));
    }
}
