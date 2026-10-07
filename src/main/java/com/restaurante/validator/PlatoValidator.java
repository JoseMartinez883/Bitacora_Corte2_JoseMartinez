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

    public void validarLimitesPorCategoria(String categoria, List<String> toppings, List<String> proteinas, List<String> salsasExtras) {
        if ("PIZZA".equalsIgnoreCase(categoria)) {
            if (toppings != null && toppings.size() > MAX_TOPPINGS) {
                throw new LimiteToppingsExcedidoException(toppings.size(), MAX_TOPPINGS);
            }
        } else if ("PASTA".equalsIgnoreCase(categoria)) {
            if (proteinas != null && proteinas.size() > 2) {
                throw new IllegalArgumentException("Límite excedido: Máximo 2 proteínas para pastas.");
            }
            if (salsasExtras != null && salsasExtras.size() > 3) {
                throw new IllegalArgumentException("Límite excedido: Máximo 3 salsas para pastas.");
            }
        }
    }

    // RF11: plato debe estar disponible para agregarse a pedido
    public void validarDisponibilidad(Plato plato) {
        if (!plato.isDisponible() || !plato.isActivo()) {
            throw new PlatoNoDisponibleException(plato.getId());
        }
    }
}
