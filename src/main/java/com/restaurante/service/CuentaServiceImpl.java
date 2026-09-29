package com.restaurante.service;

import com.restaurante.exception.CuentaNotFoundException;
import com.restaurante.exception.MesaNotFoundException;
import com.restaurante.mapper.CuentaEntityMapper;
import com.restaurante.mapper.MesaEntityMapper;
import com.restaurante.mapper.CuentaMapperOut;
import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.domain.EstadoCuenta;
import com.restaurante.model.dto.request.PagoCuentaRequestDTO;
import com.restaurante.model.dto.response.CuentaResponseDTO;
import com.restaurante.repository.CuentaRepositoryJPA;
import com.restaurante.repository.MesaRepositoryJPA;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CuentaServiceImpl implements CuentaService {
    private final CuentaRepositoryJPA cuentaRepository;
    private final MesaRepositoryJPA mesaRepository;
    private final CuentaEntityMapper cuentaEntityMapper;
    private final MesaEntityMapper mesaEntityMapper;
    private final CuentaMapperOut cuentaMapperOut;

    @Override
    public CuentaResponseDTO obtenerCuentaActivaPorMesa(Long idMesa) {
        Cuenta cuenta = cuentaRepository.findAll().stream().filter(c -> c.getIdMesa().equals(idMesa) && c.getEstado() == EstadoCuenta.ABIERTA).findFirst().map(cuentaEntityMapper::toDomain).orElseThrow(() -> new CuentaNotFoundException(idMesa));
        return cuentaMapperOut.toResponse(cuenta);
    }

    @Override
    public CuentaResponseDTO registrarPago(Long idMesa, PagoCuentaRequestDTO dto) {
        Cuenta cuenta = cuentaRepository.findAll().stream().filter(c -> c.getIdMesa().equals(idMesa) && c.getEstado() == EstadoCuenta.ABIERTA).findFirst().map(cuentaEntityMapper::toDomain).orElseThrow(() -> new CuentaNotFoundException(idMesa));
        cuenta.cerrarCuenta(dto.metodoPago());
        Mesa mesa = mesaRepository.findById(idMesa).map(mesaEntityMapper::toDomain).orElseThrow(() -> new MesaNotFoundException(idMesa));
        mesa.cerrarCuenta();
        mesaRepository.save(mesaEntityMapper.toEntity(mesa));
        return cuentaMapperOut.toResponse(cuentaEntityMapper.toDomain(cuentaRepository.save(cuentaEntityMapper.toEntity(cuenta))));
    }
}
