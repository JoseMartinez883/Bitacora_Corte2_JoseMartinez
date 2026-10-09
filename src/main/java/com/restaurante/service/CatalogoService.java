package com.restaurante.service;

import com.restaurante.model.dto.request.CatalogoRequestDTO;
import com.restaurante.model.dto.response.CatalogoResponseDTO;

import java.util.List;

public interface CatalogoService {
    CatalogoResponseDTO crearCatalogo(CatalogoRequestDTO dto);
    CatalogoResponseDTO obtenerPorIdPlato(Long idPlato);
    List<CatalogoResponseDTO> listarTodos();
    CatalogoResponseDTO actualizar(String id, CatalogoRequestDTO dto);
    void eliminar(String id);
}
