package com.restaurante.service;

import com.restaurante.exception.CatalogoNotFoundException;
import com.restaurante.model.dto.request.CatalogoRequestDTO;
import com.restaurante.model.dto.response.CatalogoResponseDTO;
import com.restaurante.persistence.document.CatalogoDocument;
import com.restaurante.repository.CatalogoRepositoryMongo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogoServiceImpl implements CatalogoService {

    private final CatalogoRepositoryMongo catalogoRepository;

    @Override
    public CatalogoResponseDTO crearCatalogo(CatalogoRequestDTO dto) {
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
        existente.setIdPlato(dto.idPlato());
        existente.setImagenesUrls(dto.imagenesUrls());
        existente.setEtiquetasComerciales(dto.etiquetasComerciales());
        return toResponse(catalogoRepository.save(existente));
    }

    @Override
    public void eliminar(String id) {
        if (!catalogoRepository.existsById(id)) {
            throw new CatalogoNotFoundException("Catálogo con id " + id + " no encontrado.");
        }
        catalogoRepository.deleteById(id);
    }

    private CatalogoResponseDTO toResponse(CatalogoDocument d) {
        return new CatalogoResponseDTO(d.getId(), d.getIdPlato(), d.getImagenesUrls(), d.getEtiquetasComerciales());
    }
}
