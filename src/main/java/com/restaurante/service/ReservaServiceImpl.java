package com.restaurante.service;

import com.restaurante.exception.ReservaNotFoundException;
import com.restaurante.mapper.ReservaEntityMapper;
import com.restaurante.mapper.ReservaMapperIn;
import com.restaurante.mapper.ReservaMapperOut;
import com.restaurante.model.domain.Reserva;
import com.restaurante.model.dto.request.ReservaRequestDTO;
import com.restaurante.model.dto.response.ReservaResponseDTO;
import com.restaurante.repository.ReservaRepositoryJPA;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {
    private final ReservaRepositoryJPA reservaRepository;
    private final ReservaEntityMapper entityMapper;
    private final ReservaMapperIn reservaMapperIn;
    private final ReservaMapperOut reservaMapperOut;

    @Override
    public ReservaResponseDTO crearReserva(ReservaRequestDTO dto) {
        Reserva reserva = reservaMapperIn.toDomain(dto);
        return reservaMapperOut.toResponse(entityMapper.toDomain(reservaRepository.save(entityMapper.toEntity(reserva))));
    }

    @Override
    public ReservaResponseDTO obtenerReservaPorId(Long id) {
        Reserva reserva = reservaRepository.findById(id).map(entityMapper::toDomain).orElseThrow(() -> new ReservaNotFoundException(id));
        return reservaMapperOut.toResponse(reserva);
    }

    @Override
    public List<ReservaResponseDTO> listarTodas() {
        return reservaRepository.findAll().stream().map(entityMapper::toDomain).map(reservaMapperOut::toResponse).toList();
    }

    @Override
    public ReservaResponseDTO cancelarReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id).map(entityMapper::toDomain).orElseThrow(() -> new ReservaNotFoundException(id));
        reserva.cancelar();
        return reservaMapperOut.toResponse(entityMapper.toDomain(reservaRepository.save(entityMapper.toEntity(reserva))));
    }

    @Override
    public ReservaResponseDTO actualizar(Long id, ReservaRequestDTO dto) {
        if (!reservaRepository.existsById(id)) throw new ReservaNotFoundException(id);
        Reserva reservaModificada = reservaMapperIn.toDomain(dto);
        reservaModificada.setId(id);
        return reservaMapperOut.toResponse(entityMapper.toDomain(reservaRepository.save(entityMapper.toEntity(reservaModificada))));
    }
}
