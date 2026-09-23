package com.restaurante.service;

import com.restaurante.exception.CuentaNotFoundException;
import com.restaurante.exception.MesaNotFoundException;
import com.restaurante.mapper.CuentaMapperOut;
import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.domain.EstadoCuenta;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.dto.request.PagoCuentaRequestDTO;
import com.restaurante.model.dto.response.CuentaResponseDTO;
import com.restaurante.repository.CuentaRepository;
import com.restaurante.repository.MesaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CuentaServiceImpl implements CuentaService {

    private final CuentaRepository cuentaRepository;
    private final MesaRepository mesaRepository;
    private final CuentaMapperOut cuentaMapperOut;

    @Override
    public CuentaResponseDTO obtenerCuentaActivaPorMesa(Long idMesa) {
        log.info("Consultando cuenta activa de mesa {}", idMesa);
        Cuenta cuenta = cuentaRepository.buscarCuentaActivaPorMesa(idMesa)
                .orElseThrow(() -> new CuentaNotFoundException(idMesa));
        return cuentaMapperOut.toResponse(cuenta);
    }

    @Override
    public CuentaResponseDTO registrarPago(Long idMesa, PagoCuentaRequestDTO dto) {
        log.info("Registrando pago ({}) en mesa {}", dto.metodoPago(), idMesa);
        Cuenta cuenta = cuentaRepository.buscarCuentaActivaPorMesa(idMesa)
                .orElseThrow(() -> new CuentaNotFoundException(idMesa));

        cuenta.cerrarCuenta(dto.metodoPago()); 

        Mesa mesa = mesaRepository.buscarPorId(idMesa)
                .orElseThrow(() -> new MesaNotFoundException(idMesa));
        mesa.cerrarCuenta(); 
        mesaRepository.guardar(mesa);

        return cuentaMapperOut.toResponse(cuentaRepository.guardar(cuenta));
    }
}
