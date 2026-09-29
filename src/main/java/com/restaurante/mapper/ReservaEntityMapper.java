package com.restaurante.mapper;

import com.restaurante.model.domain.Reserva;
import com.restaurante.persistence.entity.ReservaEntity;
import org.springframework.stereotype.Component;

@Component
public class ReservaEntityMapper {
    public ReservaEntity toEntity(Reserva domain) {
        if (domain == null) return null;
        ReservaEntity entity = new ReservaEntity();
        entity.setId(domain.getId());
        entity.setIdMesa(domain.getIdMesa());
        entity.setCliente(domain.getCliente());
        entity.setFechaHora(domain.getFechaHora());
        entity.setComensales(domain.getComensales());
        entity.setActiva(domain.isActiva());
        return entity;
    }

    public Reserva toDomain(ReservaEntity entity) {
        if (entity == null) return null;
        Reserva domain = new Reserva();
        domain.setId(entity.getId());
        domain.setIdMesa(entity.getIdMesa());
        domain.setCliente(entity.getCliente());
        domain.setFechaHora(entity.getFechaHora());
        domain.setComensales(entity.getComensales());
        domain.setActiva(entity.isActiva());
        return domain;
    }
}
