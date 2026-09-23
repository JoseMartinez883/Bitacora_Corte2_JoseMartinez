package com.restaurante.validator;

import com.restaurante.exception.LimiteToppingsExcedidoException;
import com.restaurante.exception.PlatoNoDisponibleException;
import com.restaurante.model.domain.Plato;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class PlatoValidator {

    private static final int MAX_TOPPINGS = 5;

    public void validarMasaYSalsa(String masa, String salsa) {
        if (masa == null || masa.isBlank()) {
            throw new IllegalArgumentException("La masa base es obligatoria.");
        }
        if (salsa == null || salsa.isBlank()) {
            throw new IllegalArgumentException("La salsa primaria es obligatoria.");
        }
    }

    public void validarLimiteToppings(List<String> toppings) {
        if (toppings != null && toppings.size() > MAX_TOPPINGS) {
            throw new LimiteToppingsExcedidoException(toppings.size(), MAX_TOPPINGS);
        }
    }

    // RF11: plato debe estar disponible para agregarse a pedido
    public void validarDisponibilidad(Plato plato) {
        if (!plato.isDisponible() || !plato.isActivo()) {
            throw new PlatoNoDisponibleException(plato.getId());
        }
    }
}
