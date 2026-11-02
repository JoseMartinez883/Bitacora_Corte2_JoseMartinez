package com.restaurante.repository;

import com.restaurante.persistence.entity.RegistroVehiculoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistroVehiculoRepositoryJPA extends JpaRepository<RegistroVehiculoEntity, Long> {
    long countBySalidaIsNullAndEstadoIgnoreCase(String estado);
    boolean existsByPlacaIgnoreCaseAndSalidaIsNullAndEstadoIgnoreCase(String placa, String estado);
    Optional<RegistroVehiculoEntity> findByPlacaIgnoreCaseAndSalidaIsNullAndEstadoIgnoreCase(String placa, String estado);
    List<RegistroVehiculoEntity> findBySalidaIsNullAndEstadoIgnoreCase(String estado);
}
