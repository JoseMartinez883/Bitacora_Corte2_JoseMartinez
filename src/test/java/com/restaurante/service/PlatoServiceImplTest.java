package com.restaurante.service;

import com.restaurante.exception.PlatoAlreadyExistException;
import com.restaurante.exception.PlatoNotFoundException;
import com.restaurante.mapper.PlatoEntityMapper;
import com.restaurante.mapper.PlatoMapperIn;
import com.restaurante.mapper.PlatoMapperOut;
import com.restaurante.model.domain.Plato;
import com.restaurante.persistence.entity.PlatoEntity;
import com.restaurante.model.dto.request.PlatoRequestDTO;
import com.restaurante.model.dto.response.PlatoResponseDTO;
import com.restaurante.repository.PlatoRepositoryJPA;
import com.restaurante.validator.PlatoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlatoServiceImplTest {
    @Mock private PlatoRepositoryJPA platoRepository;
    @Spy private PlatoEntityMapper entityMapper = new PlatoEntityMapper();
    @Spy private PlatoMapperIn platoMapperIn = new PlatoMapperIn();
    @Spy private PlatoMapperOut platoMapperOut = new PlatoMapperOut();
    @Mock private PlatoValidator platoValidator;
    @InjectMocks private PlatoServiceImpl platoService;

    private Plato plato;
    private PlatoEntity platoEntity;

    @BeforeEach
    void setUp() {
        plato = new Plato(); plato.setId(1L); plato.setNombre("Pizza");
        platoEntity = new PlatoEntity(); platoEntity.setId(1L); platoEntity.setNombre("Pizza"); platoEntity.setDisponible(true);
    }

    @Test
    void crearPlato_exitoso() {
        when(platoRepository.findAll()).thenReturn(List.of());
        when(platoRepository.save(any())).thenReturn(platoEntity);
        assertNotNull(platoService.crearPlato(new PlatoRequestDTO("Pizza", 1.0, "", "", "", List.of(), "")));
    }

    @Test
    void obtenerPlatoPorId_exitoso() {
        when(platoRepository.findById(1L)).thenReturn(Optional.of(platoEntity));
        assertDoesNotThrow(() -> platoService.obtenerPlatoPorId(1L));
    }

    @Test
    void listarDisponibles() {
        when(platoRepository.findAll()).thenReturn(List.of(platoEntity));
        assertFalse(platoService.listarDisponibles().isEmpty());
    }

    @Test
    void crearPlato_lanzaExcepcionSiYaExiste() {
        when(platoRepository.findAll()).thenReturn(List.of(platoEntity));
        PlatoRequestDTO dto = new PlatoRequestDTO("Pizza", 1.0, "", "", "", List.of(), "");
        assertThrows(PlatoAlreadyExistException.class, () -> platoService.crearPlato(dto));
        verify(platoRepository, never()).save(any());
    }

    @Test
    void obtenerPlatoPorId_lanzaExcepcionSiNoExiste() {
        when(platoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(PlatoNotFoundException.class, () -> platoService.obtenerPlatoPorId(99L));
    }

    @Test
    void actualizarPlato_lanzaExcepcionSiNoExiste() {
        when(platoRepository.findById(99L)).thenReturn(Optional.empty());
        PlatoRequestDTO dto = new PlatoRequestDTO("Pizza", 1.0, "", "", "", List.of(), "");
        assertThrows(PlatoNotFoundException.class, () -> platoService.actualizarPlato(99L, dto));
    }

    @Test
    void desactivarPlato_lanzaExcepcionSiNoExiste() {
        when(platoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(PlatoNotFoundException.class, () -> platoService.desactivarPlato(99L));
    }

    @Test
    void marcarAgotado_lanzaExcepcionSiNoExiste() {
        when(platoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(PlatoNotFoundException.class, () -> platoService.marcarAgotado(99L));
    }

    @Test
    void eliminar_lanzaExcepcionSiNoExiste() {
        when(platoRepository.existsById(99L)).thenReturn(false);
        assertThrows(PlatoNotFoundException.class, () -> platoService.eliminar(99L));
        verify(platoRepository, never()).deleteById(anyLong());
    }
}
