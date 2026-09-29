package com.restaurante.service;

import com.restaurante.model.dto.request.ReservaRequestDTO;
import com.restaurante.model.dto.response.ReservaResponseDTO;
import java.util.List;

public interface ReservaService {
    ReservaResponseDTO crearReserva(ReservaRequestDTO dto);
    ReservaResponseDTO obtenerReservaPorId(Long id);
    List<ReservaResponseDTO> listarTodas();
    ReservaResponseDTO cancelarReserva(Long id);
    ReservaResponseDTO actualizar(Long id, ReservaRequestDTO dto);
}
