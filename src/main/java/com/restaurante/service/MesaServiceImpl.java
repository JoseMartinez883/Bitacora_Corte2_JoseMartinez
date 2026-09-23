package com.restaurante.service;

import com.restaurante.exception.MesaNotFoundException;
import com.restaurante.mapper.MesaMapperOut;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.dto.response.MesaResponseDTO;
import com.restaurante.repository.MesaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MesaServiceImpl implements MesaService {

    private final MesaRepository mesaRepository;
    private final MesaMapperOut mesaMapperOut;

    @Override
    public List<MesaResponseDTO> listarTodas() {
        return mesaRepository.buscarTodas().stream()
                .map(mesaMapperOut::toResponse)
                .toList();
    }

    @Override
    public MesaResponseDTO obtenerMesaPorId(Long id) {
        Mesa mesa = mesaRepository.buscarPorId(id)
                .orElseThrow(() -> new MesaNotFoundException(id));
        return mesaMapperOut.toResponse(mesa);
    }

    @Override
    public MesaResponseDTO abrirCuenta(Long idMesa) {
        log.info("Abriendo cuenta en mesa {}. (RF10 / RN-06)", idMesa);
        Mesa mesa = mesaRepository.buscarPorId(idMesa)
                .orElseThrow(() -> new MesaNotFoundException(idMesa));
        mesa.abrirCuenta(); 
        return mesaMapperOut.toResponse(mesaRepository.guardar(mesa));
    }
}
