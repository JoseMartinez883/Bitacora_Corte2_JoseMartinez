package com.restaurante.service;

import com.restaurante.model.dto.request.RegistroVehiculoRequestDTO;
import com.restaurante.model.dto.response.RegistroVehiculoResponseDTO;
import java.util.List;

public interface RegistroVehiculoService {
    RegistroVehiculoResponseDTO registrarEntrada(RegistroVehiculoRequestDTO dto);
    RegistroVehiculoResponseDTO registrarSalida(Long id);
    List<RegistroVehiculoResponseDTO> listarActivos();
    List<RegistroVehiculoResponseDTO> listarTodos();
}
