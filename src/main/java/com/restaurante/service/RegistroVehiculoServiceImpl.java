package com.restaurante.service;

import com.restaurante.exception.ParqueaderoLlenoException;
import com.restaurante.exception.VehiculoNotFoundException;
import com.restaurante.exception.VehiculoYaEstacionadoException;
import com.restaurante.mapper.RegistroVehiculoEntityMapper;
import com.restaurante.mapper.RegistroVehiculoMapperIn;
import com.restaurante.mapper.RegistroVehiculoMapperOut;
import com.restaurante.model.domain.RegistroVehiculo;
import com.restaurante.model.dto.request.RegistroVehiculoRequestDTO;
import com.restaurante.model.dto.response.RegistroVehiculoResponseDTO;
import com.restaurante.repository.RegistroVehiculoRepositoryJPA;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistroVehiculoServiceImpl implements RegistroVehiculoService {

    private static final int CAPACIDAD_MAXIMA_PARQUEADERO = 20;
    private static final String ESTADO_ACTIVO = "ACTIVO";

    private final RegistroVehiculoRepositoryJPA registroRepository;
    private final RegistroVehiculoEntityMapper entityMapper;
    private final RegistroVehiculoMapperIn mapperIn;
    private final RegistroVehiculoMapperOut mapperOut;

    @Override
    public RegistroVehiculoResponseDTO registrarEntrada(RegistroVehiculoRequestDTO dto) {
        log.info("Iniciando registro de entrada para vehículo con placa: '{}'", dto.placa());

        // 1. Validar límite de cupos del parqueadero (ej. 20 cupos)
        long activos = registroRepository.findAll().stream()
                .filter(v -> v.getSalida() == null && ESTADO_ACTIVO.equalsIgnoreCase(v.getEstado()))
                .count();
        if (activos >= CAPACIDAD_MAXIMA_PARQUEADERO) {
            log.warn("Ingreso denegado: parqueadero lleno con {} cupos ocupados", CAPACIDAD_MAXIMA_PARQUEADERO);
            throw new ParqueaderoLlenoException(CAPACIDAD_MAXIMA_PARQUEADERO);
        }

        // 2. Validar que la placa no se encuentre ya activa dentro del parqueadero (Conflicto 409)
        boolean yaEstacionado = registroRepository.findAll().stream()
                .anyMatch(v -> v.getPlaca() != null && v.getPlaca().equalsIgnoreCase(dto.placa())
                        && v.getSalida() == null && ESTADO_ACTIVO.equalsIgnoreCase(v.getEstado()));
        if (yaEstacionado) {
            log.warn("Ingreso denegado: la placa '{}' ya tiene una estancia activa en el parqueadero", dto.placa());
            throw new VehiculoYaEstacionadoException(dto.placa());
        }

        RegistroVehiculo registro = mapperIn.toDomain(dto);
        var guardado = entityMapper.toDomain(registroRepository.save(entityMapper.toEntity(registro)));
        log.info("Vehículo '{}' registrado exitosamente en el parqueadero con ID: {}", guardado.getPlaca(), guardado.getId());
        return mapperOut.toResponse(guardado);
    }

    @Override
    public RegistroVehiculoResponseDTO registrarSalida(Long id) {
        log.info("Registrando salida de vehículo con ID: {}", id);
        RegistroVehiculo registro = registroRepository.findById(id).map(entityMapper::toDomain)
                .orElseThrow(() -> {
                    log.warn("Vehículo con ID {} no encontrado para registrar salida", id);
                    return new VehiculoNotFoundException(id);
                });
        registro.registrarSalida();
        var guardado = entityMapper.toDomain(registroRepository.save(entityMapper.toEntity(registro)));
        log.info("Salida registrada para vehículo con ID: {}. Cobro liquidado: ${}", id, guardado.getCobro());
        return mapperOut.toResponse(guardado);
    }

    @Override
    public RegistroVehiculoResponseDTO registrarSalidaPorPlaca(String placa) {
        log.info("Registrando salida de vehículo con placa: '{}'", placa);
        RegistroVehiculo registro = registroRepository.findAll().stream()
                .filter(v -> v.getPlaca() != null && v.getPlaca().equalsIgnoreCase(placa)
                        && v.getSalida() == null && ESTADO_ACTIVO.equalsIgnoreCase(v.getEstado()))
                .findFirst()
                .map(entityMapper::toDomain)
                .orElseThrow(() -> {
                    log.warn("No se encontró vehículo activo con placa: '{}'", placa);
                    return new VehiculoNotFoundException(placa);
                });
        registro.registrarSalida();
        var guardado = entityMapper.toDomain(registroRepository.save(entityMapper.toEntity(registro)));
        log.info("Salida registrada para vehículo con placa '{}'. Cobro liquidado: ${}", placa, guardado.getCobro());
        return mapperOut.toResponse(guardado);
    }

    @Override
    public List<RegistroVehiculoResponseDTO> listarActivos() {
        log.info("Consultando vehículos actualmente activos en el parqueadero");
        return registroRepository.findAll().stream()
                .filter(v -> v.getSalida() == null && ESTADO_ACTIVO.equalsIgnoreCase(v.getEstado()))
                .map(entityMapper::toDomain)
                .map(mapperOut::toResponse)
                .toList();
    }

    @Override
    public List<RegistroVehiculoResponseDTO> listarTodos() {
        return registroRepository.findAll().stream()
                .map(entityMapper::toDomain)
                .map(mapperOut::toResponse)
                .toList();
    }
}
