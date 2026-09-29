package com.restaurante.service;

import com.restaurante.exception.ReservaNotFoundException;
import com.restaurante.mapper.ReservaMapperIn;
import com.restaurante.mapper.ReservaMapperOut;
import com.restaurante.model.domain.Reserva;
import com.restaurante.model.dto.request.ReservaRequestDTO;
import com.restaurante.model.dto.response.ReservaResponseDTO;
import com.restaurante.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final ReservaMapperIn reservaMapperIn;
    private final ReservaMapperOut reservaMapperOut;

    @Override
    public ReservaResponseDTO crearReserva(ReservaRequestDTO dto) {
        log.info("Creando reserva para cliente: {}", dto.cliente());
        Reserva reserva = reservaMapperIn.toDomain(dto);
        return reservaMapperOut.toResponse(reservaRepository.guardar(reserva));
    }

    @Override
    public ReservaResponseDTO obtenerReservaPorId(Long id) {
        Reserva reserva = reservaRepository.buscarPorId(id)
                .orElseThrow(() -> new ReservaNotFoundException(id));
        return reservaMapperOut.toResponse(reserva);
    }

    @Override
    public List<ReservaResponseDTO> listarTodas() {
        return reservaRepository.buscarTodas().stream()
                .map(reservaMapperOut::toResponse)
                .toList();
    }

    @Override
    public ReservaResponseDTO cancelarReserva(Long id) {
        log.info("Cancelando reserva {}", id);
        Reserva reserva = reservaRepository.buscarPorId(id)
                .orElseThrow(() -> new ReservaNotFoundException(id));
        reserva.cancelar(); 
        return reservaMapperOut.toResponse(reservaRepository.guardar(reserva));
    }

    @Override
    public ReservaResponseDTO actualizar(Long id, com.restaurante.model.dto.request.ReservaRequestDTO dto) {
        log.info("Actualizando reserva con id: {}", id);

        reservaRepository.buscarPorId(id);
        com.restaurante.model.domain.Reserva reservaModificada = reservaMapperIn.toDomain(dto);
        reservaModificada.setId(id);
        reservaRepository.guardar(reservaModificada);
        return reservaMapperOut.toResponse(reservaModificada);
    }
}
