package com.restaurante.repository;

import com.restaurante.persistence.entity.PlatoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlatoRepositoryJPA extends JpaRepository<PlatoEntity, Long> {
    List<PlatoEntity> findByDisponibleTrue();
    boolean existsByNombreIgnoreCase(String nombre);
}
