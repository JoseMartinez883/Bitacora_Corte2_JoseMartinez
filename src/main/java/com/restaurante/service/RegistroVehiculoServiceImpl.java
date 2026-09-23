package com.restaurante.service;

import com.restaurante.exception.VehiculoNotFoundException;
import com.restaurante.mapper.RegistroVehiculoMapperIn;
import com.restaurante.mapper.RegistroVehiculoMapperOut;
import com.restaurante.model.domain.RegistroVehiculo;
import com.restaurante.model.dto.request.RegistroVehiculoRequestDTO;
import com.restaurante.model.dto.response.RegistroVehiculoResponseDTO;
import com.restaurante.repository.RegistroVehiculoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistroVehiculoServiceImpl implements RegistroVehiculoService {

    private final RegistroVehiculoRepository registroRepository;
    private final RegistroVehiculoMapperIn mapperIn;
    private final RegistroVehiculoMapperOut mapperOut;

    @Override
    public RegistroVehiculoResponseDTO registrarEntrada(RegistroVehiculoRequestDTO dto) {
        log.info("Registrando entrada del vehículo: {}", dto.placa());
        RegistroVehiculo registro = mapperIn.toDomain(dto);
        return mapperOut.toResponse(registroRepository.guardar(registro));
    }

    @Override
    public RegistroVehiculoResponseDTO registrarSalida(Long id) {
        log.info("Registrando salida del vehículo con registro id: {}", id);
        RegistroVehiculo registro = registroRepository.buscarPorId(id)
                .orElseThrow(() -> new VehiculoNotFoundException(id));
        registro.registrarSalida();
        return mapperOut.toResponse(registroRepository.guardar(registro));
    }

    @Override
    public List<RegistroVehiculoResponseDTO> listarActivos() {
        return registroRepository.buscarActivos().stream()
                .map(mapperOut::toResponse)
                .toList();
    }

    @Override
    public List<RegistroVehiculoResponseDTO> listarTodos() {
        return registroRepository.buscarTodos().stream()
                .map(mapperOut::toResponse)
                .toList();
    }
}
