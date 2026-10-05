package com.restaurante.mapper;

import com.restaurante.model.domain.Cuenta;
import com.restaurante.persistence.entity.CuentaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.InjectionStrategy;

@Mapper(componentModel = "spring", uses = {ItemPedidoEntityMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CuentaEntityMapper {
    CuentaEntity toEntity(Cuenta domain);
    Cuenta toDomain(CuentaEntity entity);
}
