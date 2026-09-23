package com.restaurante.repository;

import com.restaurante.model.domain.Mesa;
import com.restaurante.model.domain.EstadoMesa;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MesaRepository {

    private final List<Mesa> mesas = new ArrayList<>();
    private final AtomicLong contador = new AtomicLong(1);

    public MesaRepository() {
        for (int i = 1; i <= 6; i++) {
            Mesa mesa = new Mesa();
            mesa.setId(contador.getAndIncrement());
            mesa.setNumero(i);
            mesa.setCapacidad(4);
            mesa.setEstado(EstadoMesa.DISPONIBLE);
            mesa.setCuentaAbierta(false);
            mesas.add(mesa);
        }
    }

    public Mesa guardar(Mesa mesa) {
        if (mesa.getId() == null) {
            mesa.setId(contador.getAndIncrement());
            mesas.add(mesa);
        } else {
            mesas.replaceAll(m -> m.getId().equals(mesa.getId()) ? mesa : m);
        }
        return mesa;
    }

    public Optional<Mesa> buscarPorId(Long id) {
        return mesas.stream().filter(m -> m.getId().equals(id)).findFirst();
    }

    public List<Mesa> buscarTodas() {
        return List.copyOf(mesas);
    }

    public List<Mesa> buscarDisponibles() {
        return mesas.stream()
                .filter(m -> m.getEstado() == EstadoMesa.DISPONIBLE)
                .toList();
    }
}
