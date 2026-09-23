package com.restaurante.repository;

import com.restaurante.model.domain.Reserva;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ReservaRepository {

    private final List<Reserva> reservas = new ArrayList<>();
    private final AtomicLong contador = new AtomicLong(1);

    public Reserva guardar(Reserva reserva) {
        if (reserva.getId() == null) {
            reserva.setId(contador.getAndIncrement());
            reservas.add(reserva);
        } else {
            reservas.replaceAll(r -> r.getId().equals(reserva.getId()) ? reserva : r);
        }
        return reserva;
    }

    public Optional<Reserva> buscarPorId(Long id) {
        return reservas.stream().filter(r -> r.getId().equals(id)).findFirst();
    }

    public List<Reserva> buscarTodas() {
        return List.copyOf(reservas);
    }

    public List<Reserva> buscarActivas() {
        return reservas.stream().filter(Reserva::isActiva).toList();
    }
}
