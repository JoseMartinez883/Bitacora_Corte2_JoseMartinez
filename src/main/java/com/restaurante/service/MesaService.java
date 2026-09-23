package com.restaurante.service;

import com.restaurante.model.dto.response.MesaResponseDTO;
import java.util.List;

public interface MesaService {
    List<MesaResponseDTO> listarTodas();
    MesaResponseDTO obtenerMesaPorId(Long id);
    MesaResponseDTO abrirCuenta(Long idMesa);
}
