package com.restaurante.mapper;

import com.restaurante.model.domain.RegistroVehiculo;
import com.restaurante.persistence.entity.RegistroVehiculoEntity;
import org.springframework.stereotype.Component;

@Component
public class RegistroVehiculoEntityMapper {
    public RegistroVehiculoEntity toEntity(RegistroVehiculo domain) {
        if (domain == null) return null;
        RegistroVehiculoEntity entity = new RegistroVehiculoEntity();
        entity.setId(domain.getId());
        entity.setPlaca(domain.getPlaca());
        entity.setEntrada(domain.getEntrada());
        entity.setSalida(domain.getSalida());
        entity.setEstado(domain.getEstado());
        entity.setCobro(domain.getCobro());
        return entity;
    }

    public RegistroVehiculo toDomain(RegistroVehiculoEntity entity) {
        if (entity == null) return null;
        RegistroVehiculo domain = new RegistroVehiculo();
        domain.setId(entity.getId());
        domain.setPlaca(entity.getPlaca());
        domain.setEntrada(entity.getEntrada());
        domain.setSalida(entity.getSalida());
        domain.setEstado(entity.getEstado());
        domain.setCobro(entity.getCobro());
        return domain;
    }
}
