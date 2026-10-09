package com.restaurante.service;

import com.restaurante.model.dto.request.ResenaRequestDTO;
import com.restaurante.model.dto.response.ResenaResponseDTO;

import java.util.List;

public interface ResenaService {
    ResenaResponseDTO crearResena(ResenaRequestDTO dto);
    ResenaResponseDTO obtenerPorId(String id);
    List<ResenaResponseDTO> listarTodas();
    List<ResenaResponseDTO> listarPorCalificacionMinima(int calificacion);
    void eliminar(String id);
}
