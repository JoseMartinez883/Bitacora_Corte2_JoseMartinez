package com.restaurante.repository;

import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.domain.EstadoCuenta;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class CuentaRepository {

    private final List<Cuenta> cuentas = new ArrayList<>();
    private final AtomicLong contador = new AtomicLong(1);

    public Cuenta guardar(Cuenta cuenta) {
        if (cuenta.getId() == null) {
            cuenta.setId(contador.getAndIncrement());
            cuentas.add(cuenta);
        } else {
            cuentas.replaceAll(c -> c.getId().equals(cuenta.getId()) ? cuenta : c);
        }
        return cuenta;
    }

    public Optional<Cuenta> buscarPorId(Long id) {
        return cuentas.stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    public Optional<Cuenta> buscarCuentaActivaPorMesa(Long idMesa) {
        return cuentas.stream()
                .filter(c -> c.getIdMesa().equals(idMesa) && c.getEstado() == EstadoCuenta.ABIERTA)
                .findFirst();
    }

    public List<Cuenta> buscarTodas() {
        return List.copyOf(cuentas);
    }
}
