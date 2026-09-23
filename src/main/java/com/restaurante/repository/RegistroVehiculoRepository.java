package com.restaurante.repository;

import com.restaurante.model.domain.RegistroVehiculo;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class RegistroVehiculoRepository {

    private final List<RegistroVehiculo> registros = new ArrayList<>();
    private final AtomicLong contador = new AtomicLong(1);

    public RegistroVehiculo guardar(RegistroVehiculo registro) {
        if (registro.getId() == null) {
            registro.setId(contador.getAndIncrement());
            registros.add(registro);
        } else {
            registros.replaceAll(r -> r.getId().equals(registro.getId()) ? registro : r);
        }
        return registro;
    }

    public Optional<RegistroVehiculo> buscarPorId(Long id) {
        return registros.stream().filter(r -> r.getId().equals(id)).findFirst();
    }

    public List<RegistroVehiculo> buscarTodos() {
        return List.copyOf(registros);
    }

    public List<RegistroVehiculo> buscarActivos() {
        return registros.stream().filter(RegistroVehiculo::estaActivo).toList();
    }
}
