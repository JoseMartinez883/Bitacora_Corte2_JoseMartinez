package com.restaurante.repository;

import com.restaurante.model.domain.Plato;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PlatoRepository {

    private final List<Plato> platos = new ArrayList<>();
    private final AtomicLong contador = new AtomicLong(1);

    public Plato guardar(Plato plato) {
        if (plato.getId() == null) {
            plato.setId(contador.getAndIncrement());
            platos.add(plato);
        } else {
            platos.replaceAll(p -> p.getId().equals(plato.getId()) ? plato : p);
        }
        return plato;
    }

    public Optional<Plato> buscarPorId(Long id) {
        return platos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public List<Plato> buscarTodos() {
        return List.copyOf(platos);
    }

    public List<Plato> buscarDisponibles() {
        return platos.stream()
                .filter(p -> p.isActivo() && p.isDisponible())
                .toList();
    }

    public boolean existePorNombre(String nombre) {
        return platos.stream()
                .anyMatch(p -> p.getNombre().equalsIgnoreCase(nombre));
    }

    public void eliminar(Long id) {
        platos.removeIf(p -> p.getId().equals(id));
    }

}
