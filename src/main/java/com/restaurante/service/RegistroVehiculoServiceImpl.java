package com.restaurante.service;

import com.restaurante.exception.VehiculoNotFoundException;
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
    private final RegistroVehiculoRepositoryJPA registroRepository;
    private final RegistroVehiculoEntityMapper entityMapper;
    private final RegistroVehiculoMapperIn mapperIn;
    private final RegistroVehiculoMapperOut mapperOut;

    @Override
    public RegistroVehiculoResponseDTO registrarEntrada(RegistroVehiculoRequestDTO dto) {
        RegistroVehiculo registro = mapperIn.toDomain(dto);
        return mapperOut.toResponse(entityMapper.toDomain(registroRepository.save(entityMapper.toEntity(registro))));
    }

    @Override
    public RegistroVehiculoResponseDTO registrarSalida(Long id) {
        RegistroVehiculo registro = registroRepository.findById(id).map(entityMapper::toDomain).orElseThrow(() -> new VehiculoNotFoundException(id));
        registro.registrarSalida();
        return mapperOut.toResponse(entityMapper.toDomain(registroRepository.save(entityMapper.toEntity(registro))));
    }

    @Override
    public List<RegistroVehiculoResponseDTO> listarActivos() {
        return registroRepository.findAll().stream().filter(v -> v.getSalida() == null && "ACTIVO".equals(v.getEstado())).map(entityMapper::toDomain).map(mapperOut::toResponse).toList();
    }

    @Override
    public List<RegistroVehiculoResponseDTO> listarTodos() {
        return registroRepository.findAll().stream().map(entityMapper::toDomain).map(mapperOut::toResponse).toList();
    }
}
