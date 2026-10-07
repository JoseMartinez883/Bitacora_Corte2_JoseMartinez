package com.restaurante.repository;

import com.restaurante.persistence.entity.PlatoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatoRepositoryJPA extends JpaRepository<PlatoEntity, Long> {
    java.util.List<PlatoEntity> findByDisponibleTrue();
}