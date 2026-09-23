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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
@DisplayName("Tests del servicio PlatoServiceImpl")
class PlatoServiceImplTest {

    @Mock private PlatoRepository platoRepository;
    @Mock private PlatoMapperIn platoMapperIn;
    @Mock private PlatoMapperOut platoMapperOut;
    @Mock private PlatoValidator platoValidator;

    @InjectMocks
    private PlatoServiceImpl platoService;

    private Plato plato;
    private PlatoRequestDTO dto;
    private PlatoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        plato = new Plato();
        plato.setId(1L);
        plato.setNombre("Margherita");
        plato.setPrecio(25000.0);
        plato.setCategoria("Pizza");
        plato.setMasa("delgada");
        plato.setSalsa("tomate");
        plato.setActivo(true);
        plato.setDisponible(true);

        dto = new PlatoRequestDTO(
                "Margherita", 25000.0, "Pizza",
                "delgada", "tomate",
                List.of("mozzarella"), "La pizza clásica"
        );

        responseDTO = new PlatoResponseDTO(
                1L, "Margherita", 25000.0, "Pizza",
                "delgada", "tomate",
                List.of("mozzarella"), "La pizza clásica",
                true, true
        );
    }

    // ===== crearPlato =====

    @Test
    @DisplayName("crearPlato: debe crear y retornar el plato correctamente")
    void crearPlato_exitoso() {
        doNothing().when(platoValidator).validarMasaYSalsa(any(), any());
        doNothing().when(platoValidator).validarLimiteToppings(any());
        when(platoRepository.existePorNombre("Margherita")).thenReturn(false);
        when(platoMapperIn.toDomain(dto)).thenReturn(plato);
        when(platoRepository.guardar(plato)).thenReturn(plato);
        when(platoMapperOut.toResponse(plato)).thenReturn(responseDTO);

        PlatoResponseDTO result = platoService.crearPlato(dto);

        assertNotNull(result);
        assertEquals("Margherita", result.nombre());
        assertEquals(25000.0, result.precio());
        verify(platoValidator).validarMasaYSalsa("delgada", "tomate");
        verify(platoValidator).validarLimiteToppings(List.of("mozzarella"));
        verify(platoRepository).guardar(plato);
    }

    @Test
    @DisplayName("crearPlato: si el nombre ya existe debe lanzar PlatoAlreadyExistException")
    void crearPlato_nombreDuplicado_lanzaExcepcion() {
        doNothing().when(platoValidator).validarMasaYSalsa(any(), any());
        doNothing().when(platoValidator).validarLimiteToppings(any());
        when(platoRepository.existePorNombre("Margherita")).thenReturn(true);

        assertThrows(PlatoAlreadyExistException.class, () -> platoService.crearPlato(dto));
        verify(platoRepository, never()).guardar(any());
    }

    // ===== obtenerPlatoPorId =====

    @Test
    @DisplayName("obtenerPlatoPorId: debe retornar el plato si existe")
    void obtenerPlatoPorId_existe_retornaPlato() {
        when(platoRepository.buscarPorId(1L)).thenReturn(Optional.of(plato));
        when(platoMapperOut.toResponse(plato)).thenReturn(responseDTO);

        PlatoResponseDTO result = platoService.obtenerPlatoPorId(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    @DisplayName("obtenerPlatoPorId: si no existe debe lanzar PlatoNotFoundException")
    void obtenerPlatoPorId_noExiste_lanzaExcepcion() {
        when(platoRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(PlatoNotFoundException.class, () -> platoService.obtenerPlatoPorId(99L));
    }

    // ===== listarTodos =====

    @Test
    @DisplayName("listarTodos: debe retornar lista con todos los platos")
    void listarTodos_retornaListaCompleta() {
        when(platoRepository.buscarTodos()).thenReturn(List.of(plato));
        when(platoMapperOut.toResponse(plato)).thenReturn(responseDTO);

        List<PlatoResponseDTO> result = platoService.listarTodos();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("listarDisponibles: debe retornar solo platos activos y disponibles")
    void listarDisponibles_retornaFiltrados() {
        when(platoRepository.buscarDisponibles()).thenReturn(List.of(plato));
        when(platoMapperOut.toResponse(plato)).thenReturn(responseDTO);

        List<PlatoResponseDTO> result = platoService.listarDisponibles();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    // ===== actualizarPlato =====

    @Test
    @DisplayName("actualizarPlato: debe actualizar los campos del plato")
    void actualizarPlato_exitoso() {
        doNothing().when(platoValidator).validarMasaYSalsa(any(), any());
        doNothing().when(platoValidator).validarLimiteToppings(any());
        when(platoRepository.buscarPorId(1L)).thenReturn(Optional.of(plato));
        when(platoRepository.guardar(plato)).thenReturn(plato);
        when(platoMapperOut.toResponse(plato)).thenReturn(responseDTO);

        PlatoResponseDTO result = platoService.actualizarPlato(1L, dto);

        assertNotNull(result);
        verify(platoRepository).guardar(plato);
    }

    @Test
    @DisplayName("actualizarPlato: si el plato no existe debe lanzar PlatoNotFoundException")
    void actualizarPlato_noExiste_lanzaExcepcion() {
        when(platoRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(PlatoNotFoundException.class, () -> platoService.actualizarPlato(99L, dto));
    }

    // ===== desactivarPlato (RF9) =====

    @Test
    @DisplayName("desactivarPlato (RF9): debe desactivar el plato sin eliminarlo")
    void desactivarPlato_rf9_desactivaCorrectamente() {
        when(platoRepository.buscarPorId(1L)).thenReturn(Optional.of(plato));
        when(platoRepository.guardar(plato)).thenReturn(plato);
        when(platoMapperOut.toResponse(plato)).thenReturn(responseDTO);

        platoService.desactivarPlato(1L);

        assertFalse(plato.isActivo());
        assertFalse(plato.isDisponible());
        verify(platoRepository).guardar(plato);
    }

    // ===== marcarAgotado (RF11) =====

    @Test
    @DisplayName("marcarAgotado (RF11): debe marcar el plato como no disponible")
    void marcarAgotado_rf11_marcaNoDisponible() {
        when(platoRepository.buscarPorId(1L)).thenReturn(Optional.of(plato));
        when(platoRepository.guardar(plato)).thenReturn(plato);
        when(platoMapperOut.toResponse(plato)).thenReturn(responseDTO);

        platoService.marcarAgotado(1L);

        assertFalse(plato.isDisponible());
        verify(platoRepository).guardar(plato);
    }

    @Test
    @DisplayName("marcarAgotado: si el plato no existe debe lanzar PlatoNotFoundException")
    void marcarAgotado_noExiste_lanzaExcepcion() {
        when(platoRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(PlatoNotFoundException.class, () -> platoService.marcarAgotado(99L));
    }
}
