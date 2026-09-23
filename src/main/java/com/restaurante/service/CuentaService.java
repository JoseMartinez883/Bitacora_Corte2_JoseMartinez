package com.restaurante.service;

import com.restaurante.model.dto.request.PagoCuentaRequestDTO;
import com.restaurante.model.dto.response.CuentaResponseDTO;

public interface CuentaService {
    CuentaResponseDTO obtenerCuentaActivaPorMesa(Long idMesa);
    CuentaResponseDTO registrarPago(Long idMesa, PagoCuentaRequestDTO dto);
}
