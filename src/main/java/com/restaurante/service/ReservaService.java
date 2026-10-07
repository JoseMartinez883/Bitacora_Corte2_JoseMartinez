package com.restaurante.service;

import com.restaurante.model.dto.request.ReservaRequestDTO;
import com.restaurante.model.dto.response.ReservaResponseDTO;
import java.util.List;

import java.util.UUID;

public interface ReservaService {
    ReservaResponseDTO crearReserva(ReservaRequestDTO dto);
    ReservaResponseDTO obtenerReservaPorId(UUID id);
    List<ReservaResponseDTO> listarTodas();
    ReservaResponseDTO cancelarReserva(UUID id);
    ReservaResponseDTO actualizar(UUID id, ReservaRequestDTO dto);
}
