package com.restaurante.service;

import com.restaurante.exception.CatalogoNotFoundException;
import com.restaurante.model.dto.request.CatalogoRequestDTO;
import com.restaurante.model.dto.response.CatalogoResponseDTO;
import com.restaurante.persistence.document.CatalogoDocument;
import com.restaurante.repository.CatalogoRepositoryMongo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogoServiceImplTest {

    @Mock private CatalogoRepositoryMongo catalogoRepository;
    @InjectMocks private CatalogoServiceImpl catalogoService;

    private CatalogoDocument doc() {
        return CatalogoDocument.builder().id("c1").idPlato(1L)
                .imagenesUrls(List.of("http://img/1.jpg"))
                .etiquetasComerciales(List.of("vegano")).build();
    }

    private CatalogoRequestDTO req() {
        return new CatalogoRequestDTO(1L, List.of("http://img/1.jpg"), List.of("vegano"));
    }

    @Test
    void crearCatalogo_guardaEnMongo() {
        when(catalogoRepository.save(any(CatalogoDocument.class))).thenAnswer(i -> {
            CatalogoDocument d = i.getArgument(0);
            d.setId("c1");
            return d;
        });
        CatalogoResponseDTO res = catalogoService.crearCatalogo(req());
        assertEquals("c1", res.id());
        assertEquals(1L, res.idPlato());
        assertEquals(1, res.imagenesUrls().size());
    }

    @Test
    void obtenerPorIdPlato_exitoso() {
        when(catalogoRepository.findByIdPlato(1L)).thenReturn(Optional.of(doc()));
        assertEquals("c1", catalogoService.obtenerPorIdPlato(1L).id());
    }

    @Test
    void obtenerPorIdPlato_lanzaExcepcionSiNoExiste() {
        when(catalogoRepository.findByIdPlato(99L)).thenReturn(Optional.empty());
        assertThrows(CatalogoNotFoundException.class, () -> catalogoService.obtenerPorIdPlato(99L));
    }

    @Test
    void listarTodos_exitoso() {
        when(catalogoRepository.findAll()).thenReturn(List.of(doc()));
        assertEquals(1, catalogoService.listarTodos().size());
    }

    @Test
    void actualizar_exitoso() {
        when(catalogoRepository.findById("c1")).thenReturn(Optional.of(doc()));
        when(catalogoRepository.save(any(CatalogoDocument.class))).thenAnswer(i -> i.getArgument(0));
        CatalogoRequestDTO nuevo = new CatalogoRequestDTO(2L, List.of("a.jpg", "b.jpg"), List.of());
        CatalogoResponseDTO res = catalogoService.actualizar("c1", nuevo);
        assertEquals(2L, res.idPlato());
        assertEquals(2, res.imagenesUrls().size());
    }

    @Test
    void actualizar_lanzaExcepcionSiNoExiste() {
        when(catalogoRepository.findById("x")).thenReturn(Optional.empty());
        CatalogoRequestDTO r = req();
        assertThrows(CatalogoNotFoundException.class, () -> catalogoService.actualizar("x", r));
    }

    @Test
    void eliminar_exitoso() {
        when(catalogoRepository.existsById("c1")).thenReturn(true);
        catalogoService.eliminar("c1");
        verify(catalogoRepository).deleteById("c1");
    }

    @Test
    void eliminar_lanzaExcepcionSiNoExiste() {
        when(catalogoRepository.existsById("x")).thenReturn(false);
        assertThrows(CatalogoNotFoundException.class, () -> catalogoService.eliminar("x"));
        verify(catalogoRepository, never()).deleteById(any());
    }
}
