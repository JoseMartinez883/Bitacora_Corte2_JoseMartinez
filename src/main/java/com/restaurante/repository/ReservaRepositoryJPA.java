package com.restaurante.repository;

import com.restaurante.persistence.entity.ReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface ReservaRepositoryJPA extends JpaRepository<ReservaEntity, UUID> {

    @Query("SELECT COUNT(r) > 0 FROM ReservaEntity r WHERE r.activa = true AND r.idMesa = :idMesa AND r.fechaHora > :inicio AND r.fechaHora < :fin")
    boolean existeConflicto(@Param("idMesa") Long idMesa, @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT COUNT(r) > 0 FROM ReservaEntity r WHERE r.activa = true AND r.id != :id AND r.idMesa = :idMesa AND r.fechaHora > :inicio AND r.fechaHora < :fin")
    boolean existeConflictoExcluyendo(@Param("id") UUID id, @Param("idMesa") Long idMesa, @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
}
