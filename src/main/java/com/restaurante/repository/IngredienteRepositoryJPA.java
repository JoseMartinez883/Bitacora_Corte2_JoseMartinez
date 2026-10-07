package com.restaurante.repository;

import com.restaurante.persistence.entity.IngredienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IngredienteRepositoryJPA extends JpaRepository<IngredienteEntity, Long> {
    Optional<IngredienteEntity> findByNombre(String nombre);
}
