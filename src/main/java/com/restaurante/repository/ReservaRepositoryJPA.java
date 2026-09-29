package com.restaurante.repository;

import com.restaurante.persistence.entity.ReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservaRepositoryJPA extends JpaRepository<ReservaEntity, Long> {
}