package com.restaurante.repository;

import com.restaurante.persistence.entity.RegistroVehiculoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroVehiculoRepositoryJPA extends JpaRepository<RegistroVehiculoEntity, Long> {
}