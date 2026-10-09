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
        log.info("Iniciando creación de plato con nombre '{}', categoría '{}'", dto.nombre(), dto.categoria());
        platoValidator.validarMasaYSalsa(dto.masa(), dto.salsa());
        platoValidator.validarLimitesPorCategoria(dto.categoria(), dto.toppings(), dto.proteinas(), dto.salsasExtras());
        boolean existe = platoRepository.findAll().stream().anyMatch(p -> p.getNombre().equalsIgnoreCase(dto.nombre()));
        if (existe) {
            log.warn("Fallo al crear plato: ya existe un plato con el nombre '{}'", dto.nombre());
            throw new PlatoAlreadyExistException(dto.nombre());
        }
        Plato plato = platoMapperIn.toDomain(dto);
        Plato guardado = entityMapper.toDomain(platoRepository.save(entityMapper.toEntity(plato)));
        log.info("Plato '{}' creado exitosamente con ID: {}", guardado.getNombre(), guardado.getId());
        return platoMapperOut.toResponse(guardado);
    }

    @Override
    public PlatoResponseDTO obtenerPlatoPorId(Long id) {
        log.debug("Consultando plato por ID: {}", id);
        Plato plato = platoRepository.findById(id).map(entityMapper::toDomain).orElseThrow(() -> {
            log.warn("Plato no encontrado con ID: {}", id);
            return new PlatoNotFoundException(id);
        });
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
        log.info("Actualizando plato con ID: {}", id);
        Plato plato = platoRepository.findById(id).map(entityMapper::toDomain).orElseThrow(() -> {
            log.warn("Intento de actualizar plato inexistente con ID: {}", id);
            return new PlatoNotFoundException(id);
        });
        platoValidator.validarMasaYSalsa(dto.masa(), dto.salsa());
        platoValidator.validarLimitesPorCategoria(dto.categoria(), dto.toppings(), dto.proteinas(), dto.salsasExtras());
        plato.setNombre(dto.nombre()); plato.setPrecio(dto.precio()); plato.setCategoria(dto.categoria()); plato.setMasa(dto.masa()); plato.setSalsa(dto.salsa()); plato.setToppings(dto.toppings()); plato.setProteinas(dto.proteinas()); plato.setSalsasExtras(dto.salsasExtras()); plato.setDescripcion(dto.descripcion());
        return platoMapperOut.toResponse(entityMapper.toDomain(platoRepository.save(entityMapper.toEntity(plato))));
    }

    @Override
    public PlatoResponseDTO desactivarPlato(Long id) {
        log.info("Desactivando plato con ID: {}", id);
        Plato plato = platoRepository.findById(id).map(entityMapper::toDomain).orElseThrow(() -> {
            log.warn("Intento de desactivar plato inexistente con ID: {}", id);
            return new PlatoNotFoundException(id);
        });
        plato.setActivo(false); plato.setDisponible(false);
        return platoMapperOut.toResponse(entityMapper.toDomain(platoRepository.save(entityMapper.toEntity(plato))));
    }

    @Override
    public PlatoResponseDTO marcarAgotado(Long id) {
        log.warn("Marcando plato con ID: {} como agotado (no disponible)", id);
        Plato plato = platoRepository.findById(id).map(entityMapper::toDomain).orElseThrow(() -> {
            log.warn("Intento de marcar como agotado plato inexistente con ID: {}", id);
            return new PlatoNotFoundException(id);
        });
        plato.cambiarDisponibilidad(false);
        return platoMapperOut.toResponse(entityMapper.toDomain(platoRepository.save(entityMapper.toEntity(plato))));
    }

    @Override
    public void eliminar(Long id) {
        log.info("Eliminando plato con ID: {}", id);
        if (!platoRepository.existsById(id)) {
            log.warn("No se puede eliminar: plato con ID {} no encontrado", id);
            throw new PlatoNotFoundException(id);
        }
        platoRepository.deleteById(id);
        log.info("Plato con ID: {} eliminado exitosamente", id);
    }
}
