package com.restaurante.mapper;

import com.restaurante.model.domain.Cuenta;
import com.restaurante.persistence.entity.CuentaEntity;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CuentaEntityMapper {
    
    private final ItemPedidoEntityMapper itemMapper;

    public CuentaEntity toEntity(Cuenta domain) {
        if (domain == null) return null;
        CuentaEntity entity = new CuentaEntity();
        entity.setId(domain.getId());
        entity.setIdMesa(domain.getIdMesa());
        entity.setTotal(domain.getTotal());
        entity.setEstado(domain.getEstado());
        entity.setFechaApertura(domain.getFechaApertura());
        entity.setFechaCierre(domain.getFechaCierre());
        if (domain.getItems() != null) {
            entity.setItems(domain.getItems().stream().map(itemMapper::toEntity).collect(Collectors.toList()));
        }
        entity.setMetodoPago(domain.getMetodoPago());
        return entity;
    }

    public Cuenta toDomain(CuentaEntity entity) {
        if (entity == null) return null;
        Cuenta domain = new Cuenta();
        domain.setId(entity.getId());
        domain.setIdMesa(entity.getIdMesa());
        domain.setTotal(entity.getTotal());
        domain.setEstado(entity.getEstado());
        domain.setFechaApertura(entity.getFechaApertura());
        domain.setFechaCierre(entity.getFechaCierre());
        if (entity.getItems() != null) {
            domain.setItems(entity.getItems().stream().map(itemMapper::toDomain).collect(Collectors.toList()));
        }
        domain.setMetodoPago(entity.getMetodoPago());
        return domain;
    }
}
