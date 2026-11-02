package com.restaurante.service;

import com.restaurante.exception.MesaNotFoundException;
import com.restaurante.mapper.MesaEntityMapper;
import com.restaurante.mapper.MesaMapperOut;
import com.restaurante.model.domain.EstadoCuenta;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.dto.response.MesaResponseDTO;
import com.restaurante.persistence.entity.CuentaEntity;
import com.restaurante.repository.CuentaRepositoryJPA;
import com.restaurante.repository.MesaRepositoryJPA;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MesaServiceImpl implements MesaService {
    private final MesaRepositoryJPA mesaRepository;
    private final CuentaRepositoryJPA cuentaRepository;
    private final MesaEntityMapper mesaEntityMapper;
    private final MesaMapperOut mesaMapperOut;

    @Override
    public List<MesaResponseDTO> listarTodas() {
        log.info("Listando todas las mesas registradas");
        return mesaRepository.findAll().stream().map(mesaEntityMapper::toDomain).map(mesaMapperOut::toResponse).toList();
    }

    @Override
    public MesaResponseDTO obtenerMesaPorId(Long id) {
        log.debug("Consultando mesa con ID: {}", id);
        Mesa mesa = mesaRepository.findById(id).map(mesaEntityMapper::toDomain).orElseThrow(() -> {
            log.warn("Mesa no encontrada con ID: {}", id);
            return new MesaNotFoundException(id);
        });
        return mesaMapperOut.toResponse(mesa);
    }

    @Override
    @Transactional
    public MesaResponseDTO abrirCuenta(Long idMesa) {
        log.info("Iniciando apertura de cuenta para mesa ID: {}", idMesa);
        Mesa mesa = mesaRepository.findById(idMesa).map(mesaEntityMapper::toDomain).orElseThrow(() -> {
            log.warn("Fallo al abrir cuenta: mesa ID {} no encontrada", idMesa);
            return new MesaNotFoundException(idMesa);
        });
        mesa.abrirCuenta();
        var guardada = mesaEntityMapper.toDomain(mesaRepository.save(mesaEntityMapper.toEntity(mesa)));

        // Persistir la cuenta en estado ABIERTA
        CuentaEntity nuevaCuenta = new CuentaEntity();
        nuevaCuenta.setIdMesa(idMesa);
        nuevaCuenta.setEstado(EstadoCuenta.ABIERTA);
        nuevaCuenta.setFechaApertura(LocalDateTime.now(ZoneId.systemDefault()));
        nuevaCuenta.setTotal(0.0);
        cuentaRepository.save(nuevaCuenta);

        log.info("Cuenta abierta exitosamente para mesa ID: {}", idMesa);
        return mesaMapperOut.toResponse(guardada);
    }
}
