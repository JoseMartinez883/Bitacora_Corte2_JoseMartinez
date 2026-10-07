package com.restaurante.repository;

import com.restaurante.persistence.entity.CuentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuentaRepositoryJPA extends JpaRepository<CuentaEntity, Long> {
    java.util.Optional<CuentaEntity> findByIdMesaAndEstado(Long idMesa, com.restaurante.model.domain.EstadoCuenta estado);
}