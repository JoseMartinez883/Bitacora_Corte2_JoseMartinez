package com.restaurante.service;

import com.restaurante.model.dto.request.PlatoRequestDTO;
import com.restaurante.model.dto.response.PlatoResponseDTO;
import java.util.List;

public interface PlatoService {
    PlatoResponseDTO crearPlato(PlatoRequestDTO dto);
    PlatoResponseDTO obtenerPlatoPorId(Long id);
    List<PlatoResponseDTO> listarTodos();
    List<PlatoResponseDTO> listarDisponibles();
    PlatoResponseDTO actualizarPlato(Long id, PlatoRequestDTO dto);
    PlatoResponseDTO desactivarPlato(Long id);
    PlatoResponseDTO marcarAgotado(Long id);
}
