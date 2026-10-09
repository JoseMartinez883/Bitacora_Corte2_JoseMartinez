package com.restaurante.validator;

import com.restaurante.exception.LimiteToppingsExcedidoException;
import com.restaurante.exception.PlatoNoDisponibleException;
import com.restaurante.model.domain.Plato;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests del PlatoValidator — Reglas de negocio RN-01, RN-02, RF11")
class PlatoValidatorTest {

    private PlatoValidator platoValidator;

    @BeforeEach
    void setUp() {
        platoValidator = new PlatoValidator();
    }

    // ===== validarMasaYSalsa (RN-01) =====

    @Test
    @DisplayName("RN-01: masa y salsa válidas — no debe lanzar excepción")
    void validarMasaYSalsa_valido_noLanzaExcepcion() {
        assertDoesNotThrow(() -> platoValidator.validarMasaYSalsa("delgada", "tomate"));
    }

    @Test
    @DisplayName("RN-01: masa vacía — debe lanzar IllegalArgumentException")
    void validarMasaYSalsa_masaVacia_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> platoValidator.validarMasaYSalsa("", "tomate"));
        assertTrue(ex.getMessage().contains("masa"));
    }

    @Test
    @DisplayName("RN-01: masa null — debe lanzar IllegalArgumentException")
    void validarMasaYSalsa_masaNull_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> platoValidator.validarMasaYSalsa(null, "tomate"));
    }

    @Test
    @DisplayName("RN-01: salsa vacía — debe lanzar IllegalArgumentException")
    void validarMasaYSalsa_salsaVacia_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> platoValidator.validarMasaYSalsa("delgada", ""));
        assertTrue(ex.getMessage().contains("salsa"));
    }

    @Test
    @DisplayName("RN-01: salsa null — debe lanzar IllegalArgumentException")
    void validarMasaYSalsa_salsaNull_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> platoValidator.validarMasaYSalsa("delgada", null));
    }

    // ===== validarLimiteToppings (RN-02) =====

    @Test
    @DisplayName("RN-02: 5 toppings (límite exacto) — no debe lanzar excepción")
    void validarLimiteToppings_exactamenteCinco_noLanzaExcepcion() {
        List<String> toppings = List.of("1", "2", "3", "4", "5");
        assertDoesNotThrow(() -> platoValidator.validarLimiteToppings(toppings));
    }

    @Test
    @DisplayName("RN-02: 6 toppings — debe lanzar LimiteToppingsExcedidoException")
    void validarLimiteToppings_seisToppings_lanzaExcepcion() {
        List<String> toppings = List.of("1", "2", "3", "4", "5", "6");
        assertThrows(LimiteToppingsExcedidoException.class,
                () -> platoValidator.validarLimiteToppings(toppings));
    }

    @Test
    @DisplayName("RN-02: lista null — no debe lanzar excepción")
    void validarLimiteToppings_null_noLanzaExcepcion() {
        assertDoesNotThrow(() -> platoValidator.validarLimiteToppings(null));
    }

    @Test
    @DisplayName("RN-02: lista vacía — no debe lanzar excepción")
    void validarLimiteToppings_vacia_noLanzaExcepcion() {
        assertDoesNotThrow(() -> platoValidator.validarLimiteToppings(List.of()));
    }

    // ===== validarDisponibilidad (RF11) =====

    @Test
    @DisplayName("RF11: plato activo y disponible — no debe lanzar excepción")
    void validarDisponibilidad_activo_noLanzaExcepcion() {
        Plato plato = buildPlato(true, true);
        assertDoesNotThrow(() -> platoValidator.validarDisponibilidad(plato));
    }

    @Test
    @DisplayName("RF11: plato marcado como agotado (disponible=false) — debe lanzar excepción")
    void validarDisponibilidad_noDisponible_lanzaExcepcion() {
        Plato plato = buildPlato(false, true);
        assertThrows(PlatoNoDisponibleException.class,
                () -> platoValidator.validarDisponibilidad(plato));
    }

    @Test
    @DisplayName("RF11: plato desactivado (activo=false) — debe lanzar excepción")
    void validarDisponibilidad_noActivo_lanzaExcepcion() {
        Plato plato = buildPlato(true, false);
        assertThrows(PlatoNoDisponibleException.class,
                () -> platoValidator.validarDisponibilidad(plato));
    }

    @Test
    @DisplayName("validarLimitesPorCategoria: PIZZA con demasiados toppings")
    void validarLimitesPorCategoria_pizzaToppingsExcedidos_lanzaExcepcion() {
        List<String> toppings = List.of("1", "2", "3", "4", "5", "6");
        assertThrows(LimiteToppingsExcedidoException.class,
                () -> platoValidator.validarLimitesPorCategoria("PIZZA", toppings, null, null));
    }

    @Test
    @DisplayName("validarLimitesPorCategoria: PIZZA con toppings validos")
    void validarLimitesPorCategoria_pizzaToppingsValidos_noLanzaExcepcion() {
        List<String> toppings = List.of("1", "2", "3");
        assertDoesNotThrow(() -> platoValidator.validarLimitesPorCategoria("PIZZA", toppings, null, null));
    }

    @Test
    @DisplayName("validarLimitesPorCategoria: PASTA con demasiadas proteinas")
    void validarLimitesPorCategoria_pastaProteinasExcedidas_lanzaExcepcion() {
        List<String> proteinas = List.of("P1", "P2", "P3");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> platoValidator.validarLimitesPorCategoria("PASTA", null, proteinas, null));
        assertTrue(ex.getMessage().contains("Límite excedido"));
    }

    @Test
    @DisplayName("validarLimitesPorCategoria: PASTA con demasiadas salsas")
    void validarLimitesPorCategoria_pastaSalsasExcedidas_lanzaExcepcion() {
        List<String> salsas = List.of("S1", "S2", "S3", "S4");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> platoValidator.validarLimitesPorCategoria("PASTA", null, null, salsas));
        assertTrue(ex.getMessage().contains("Límite excedido"));
    }

    @Test
    @DisplayName("validarLimitesPorCategoria: PASTA con validos")
    void validarLimitesPorCategoria_pastaValida_noLanzaExcepcion() {
        List<String> proteinas = List.of("P1", "P2");
        List<String> salsas = List.of("S1", "S2", "S3");
        assertDoesNotThrow(() -> platoValidator.validarLimitesPorCategoria("PASTA", null, proteinas, salsas));
    }

    @Test
    @DisplayName("validarLimitesPorCategoria: Otra categoria")
    void validarLimitesPorCategoria_otraCategoria_noHaceNada() {
        assertDoesNotThrow(() -> platoValidator.validarLimitesPorCategoria("BEBIDA", null, null, null));
    }

    // ===== helpers =====

    private Plato buildPlato(boolean disponible, boolean activo) {
        Plato plato = new Plato();
        plato.setId(1L);
        plato.setDisponible(disponible);
        plato.setActivo(activo);
        return plato;
    }
}
