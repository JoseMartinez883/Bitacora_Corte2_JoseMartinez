package com.restaurante.service;

import com.restaurante.exception.PlatoAlreadyExistException;
import com.restaurante.exception.PlatoNotFoundException;
import com.restaurante.mapper.PlatoEntityMapper;
import com.restaurante.mapper.PlatoMapperIn;
import com.restaurante.mapper.PlatoMapperOut;
import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.request.PlatoRequestDTO;
import com.restaurante.model.dto.response.PlatoResponseDTO;
import com.restaurante.repository.PlatoRepositoryJPA;
import com.restaurante.validator.PlatoValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlatoServiceImpl implements PlatoService {

    private final PlatoRepositoryJPA platoRepository;
    private final PlatoEntityMapper entityMapper;
    private final PlatoMapperIn platoMapperIn;
    private final PlatoMapperOut platoMapperOut;
    private final PlatoValidator platoValidator;

    @Override
    public PlatoResponseDTO crearPlato(PlatoRequestDTO dto) {
        platoValidator.validarMasaYSalsa(dto.masa(), dto.salsa());
        platoValidator.validarLimiteToppings(dto.toppings());
        boolean existe = platoRepository.findAll().stream().anyMatch(p -> p.getNombre().equalsIgnoreCase(dto.nombre()));
        if (existe) throw new PlatoAlreadyExistException(dto.nombre());
        Plato plato = platoMapperIn.toDomain(dto);
        Plato guardado = entityMapper.toDomain(platoRepository.save(entityMapper.toEntity(plato)));
        return platoMapperOut.toResponse(guardado);
    }

    @Override
    public PlatoResponseDTO obtenerPlatoPorId(Long id) {
        Plato plato = platoRepository.findById(id).map(entityMapper::toDomain).orElseThrow(() -> new PlatoNotFoundException(id));
        return platoMapperOut.toResponse(plato);
    }

    @Override
    public List<PlatoResponseDTO> listarTodos() {
        return platoRepository.findAll().stream().map(entityMapper::toDomain).map(platoMapperOut::toResponse).toList();
    }

    @Override
    public List<PlatoResponseDTO> listarDisponibles() {
        return platoRepository.findByDisponibleTrue().stream().map(entityMapper::toDomain).map(platoMapperOut::toResponse).toList();
    }

    @Override
    public PlatoResponseDTO actualizarPlato(Long id, PlatoRequestDTO dto) {
        Plato plato = platoRepository.findById(id).map(entityMapper::toDomain).orElseThrow(() -> new PlatoNotFoundException(id));
        platoValidator.validarMasaYSalsa(dto.masa(), dto.salsa());
        platoValidator.validarLimiteToppings(dto.toppings());
        plato.setNombre(dto.nombre()); plato.setPrecio(dto.precio()); plato.setCategoria(dto.categoria()); plato.setMasa(dto.masa()); plato.setSalsa(dto.salsa()); plato.setToppings(dto.toppings()); plato.setDescripcion(dto.descripcion());
        return platoMapperOut.toResponse(entityMapper.toDomain(platoRepository.save(entityMapper.toEntity(plato))));
    }

    @Override
    public PlatoResponseDTO desactivarPlato(Long id) {
        Plato plato = platoRepository.findById(id).map(entityMapper::toDomain).orElseThrow(() -> new PlatoNotFoundException(id));
        plato.setActivo(false); plato.setDisponible(false);
        return platoMapperOut.toResponse(entityMapper.toDomain(platoRepository.save(entityMapper.toEntity(plato))));
    }

    @Override
    public PlatoResponseDTO marcarAgotado(Long id) {
        Plato plato = platoRepository.findById(id).map(entityMapper::toDomain).orElseThrow(() -> new PlatoNotFoundException(id));
        plato.cambiarDisponibilidad(false);
        return platoMapperOut.toResponse(entityMapper.toDomain(platoRepository.save(entityMapper.toEntity(plato))));
    }

    @Override
    public void eliminar(Long id) {
        if (!platoRepository.existsById(id)) throw new PlatoNotFoundException(id);
        platoRepository.deleteById(id);
    }
}
