package com.restaurante.service;

import com.restaurante.exception.PlatoAlreadyExistException;
import com.restaurante.exception.PlatoNotFoundException;
import com.restaurante.mapper.PlatoMapperIn;
import com.restaurante.mapper.PlatoMapperOut;
import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.request.PlatoRequestDTO;
import com.restaurante.model.dto.response.PlatoResponseDTO;
import com.restaurante.repository.PlatoRepository;
import com.restaurante.validator.PlatoValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlatoServiceImpl implements PlatoService {

    private final PlatoRepository platoRepository;
    private final PlatoMapperIn platoMapperIn;
    private final PlatoMapperOut platoMapperOut;
    private final PlatoValidator platoValidator;

    @Override
    public PlatoResponseDTO crearPlato(PlatoRequestDTO dto) {
        log.info("Creando plato: {}", dto.nombre());
        platoValidator.validarMasaYSalsa(dto.masa(), dto.salsa());
        platoValidator.validarLimiteToppings(dto.toppings());
        if (platoRepository.existePorNombre(dto.nombre())) {
            throw new PlatoAlreadyExistException(dto.nombre());
        }
        Plato plato = platoMapperIn.toDomain(dto);
        Plato guardado = platoRepository.guardar(plato);
        log.info("Plato creado con id: {}", guardado.getId());
        return platoMapperOut.toResponse(guardado);
    }

    @Override
    public PlatoResponseDTO obtenerPlatoPorId(Long id) {
        log.info("Buscando plato con id: {}", id);
        Plato plato = platoRepository.buscarPorId(id)
                .orElseThrow(() -> new PlatoNotFoundException(id));
        return platoMapperOut.toResponse(plato);
    }

    @Override
    public List<PlatoResponseDTO> listarTodos() {
        log.info("Listando todos los platos.");
        return platoRepository.buscarTodos().stream()
                .map(platoMapperOut::toResponse)
                .toList();
    }

    @Override
    public List<PlatoResponseDTO> listarDisponibles() {
        log.info("Listando platos disponibles (menú del cliente).");
        return platoRepository.buscarDisponibles().stream()
                .map(platoMapperOut::toResponse)
                .toList();
    }

    @Override
    public PlatoResponseDTO actualizarPlato(Long id, PlatoRequestDTO dto) {
        log.info("Actualizando plato con id: {}", id);
        Plato plato = platoRepository.buscarPorId(id)
                .orElseThrow(() -> new PlatoNotFoundException(id));
        platoValidator.validarMasaYSalsa(dto.masa(), dto.salsa());
        platoValidator.validarLimiteToppings(dto.toppings());
        plato.setNombre(dto.nombre());
        plato.setPrecio(dto.precio());
        plato.setCategoria(dto.categoria());
        plato.setMasa(dto.masa());
        plato.setSalsa(dto.salsa());
        plato.setToppings(dto.toppings());
        plato.setDescripcion(dto.descripcion());
        return platoMapperOut.toResponse(platoRepository.guardar(plato));
    }

    @Override
    public PlatoResponseDTO desactivarPlato(Long id) {
        log.info("Desactivando plato con id: {} (RF9)", id);
        Plato plato = platoRepository.buscarPorId(id)
                .orElseThrow(() -> new PlatoNotFoundException(id));
        plato.setActivo(false);
        plato.setDisponible(false);
        return platoMapperOut.toResponse(platoRepository.guardar(plato));
    }

    @Override
    public PlatoResponseDTO marcarAgotado(Long id) {
        log.info("Marcando plato {} como agotado (RF11)", id);
        Plato plato = platoRepository.buscarPorId(id)
                .orElseThrow(() -> new PlatoNotFoundException(id));
        plato.cambiarDisponibilidad(false);
        return platoMapperOut.toResponse(platoRepository.guardar(plato));
    }
}
