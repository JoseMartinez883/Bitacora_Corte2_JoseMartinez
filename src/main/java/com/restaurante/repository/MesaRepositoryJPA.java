package com.restaurante.repository;

import com.restaurante.persistence.entity.MesaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MesaRepositoryJPA extends JpaRepository<MesaEntity, Long> {
}