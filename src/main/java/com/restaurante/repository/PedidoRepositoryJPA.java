package com.restaurante.repository;

import com.restaurante.persistence.entity.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PedidoRepositoryJPA extends JpaRepository<PedidoEntity, Long> {
    List<PedidoEntity> findByIdMesa(Long idMesa);
}