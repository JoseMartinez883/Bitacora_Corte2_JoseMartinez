package com.restaurante.mapper;

import com.restaurante.model.domain.Mesa;
import com.restaurante.persistence.entity.MesaEntity;
import org.springframework.stereotype.Component;

@Component
public class MesaEntityMapper {
    public MesaEntity toEntity(Mesa domain) {
        if (domain == null) return null;
        MesaEntity entity = new MesaEntity();
        entity.setId(domain.getId());
        entity.setNumero(domain.getNumero());
        entity.setCapacidad(domain.getCapacidad());
        entity.setEstado(domain.getEstado());
        entity.setCuentaAbierta(domain.isCuentaAbierta());
        return entity;
    }

    public Mesa toDomain(MesaEntity entity) {
        if (entity == null) return null;
        Mesa domain = new Mesa();
        domain.setId(entity.getId());
        domain.setNumero(entity.getNumero());
        domain.setCapacidad(entity.getCapacidad());
        domain.setEstado(entity.getEstado());
        domain.setCuentaAbierta(entity.isCuentaAbierta());
        return domain;
    }
}
