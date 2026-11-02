package com.restaurante.service;

import com.restaurante.exception.CatalogoNotFoundException;
import com.restaurante.exception.PlatoNotFoundException;
import com.restaurante.model.dto.request.CatalogoRequestDTO;
import com.restaurante.model.dto.response.CatalogoResponseDTO;
import com.restaurante.persistence.document.CatalogoDocument;
import com.restaurante.repository.CatalogoRepositoryMongo;
import com.restaurante.repository.PlatoRepositoryJPA;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogoServiceImpl implements CatalogoService {

    private final CatalogoRepositoryMongo catalogoRepository;
    private final PlatoRepositoryJPA platoRepository;

    @Override
    public CatalogoResponseDTO crearCatalogo(CatalogoRequestDTO dto) {
        if (dto.idPlato() != null && !platoRepository.existsById(dto.idPlato())) {
            log.warn("Fallo al crear catálogo: no existe plato con ID {}", dto.idPlato());
            throw new PlatoNotFoundException(dto.idPlato());
        }

        CatalogoDocument doc = CatalogoDocument.builder()
                .idPlato(dto.idPlato())
                .imagenesUrls(dto.imagenesUrls())
                .etiquetasComerciales(dto.etiquetasComerciales())
                .build();
        CatalogoDocument guardado = catalogoRepository.save(doc);
        log.info("Catálogo guardado en MongoDB con id {}", guardado.getId());
        return toResponse(guardado);
    }

    @Override
    public CatalogoResponseDTO obtenerPorIdPlato(Long idPlato) {
        return catalogoRepository.findByIdPlato(idPlato)
                .map(this::toResponse)
                .orElseThrow(() -> new CatalogoNotFoundException(
                        "Catálogo para el plato " + idPlato + " no encontrado."));
    }

    @Override
    public List<CatalogoResponseDTO> listarTodos() {
        return catalogoRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public CatalogoResponseDTO actualizar(String id, CatalogoRequestDTO dto) {
        CatalogoDocument existente = catalogoRepository.findById(id)
                .orElseThrow(() -> new CatalogoNotFoundException("Catálogo con id " + id + " no encontrado."));

        if (dto.idPlato() != null && !platoRepository.existsById(dto.idPlato())) {
            log.warn("Fallo al actualizar catálogo: no existe plato con ID {}", dto.idPlato());
            throw new PlatoNotFoundException(dto.idPlato());
        }

        existente.setIdPlato(dto.idPlato());
        existente.setImagenesUrls(dto.imagenesUrls());
        existente.setEtiquetasComerciales(dto.etiquetasComerciales());
        CatalogoDocument guardado = catalogoRepository.save(existente);
        log.info("Catálogo con id {} actualizado en MongoDB", id);
        return toResponse(guardado);
    }

    @Override
    public void eliminar(String id) {
        if (!catalogoRepository.existsById(id)) {
            throw new CatalogoNotFoundException("Catálogo con id " + id + " no encontrado.");
        }
        catalogoRepository.deleteById(id);
        log.info("Catálogo con id {} eliminado de MongoDB", id);
    }

    private CatalogoResponseDTO toResponse(CatalogoDocument doc) {
        return new CatalogoResponseDTO(
                doc.getId(),
                doc.getIdPlato(),
                doc.getImagenesUrls(),
                doc.getEtiquetasComerciales()
        );
    }
}
