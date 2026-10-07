package com.restaurante.repository;

import com.restaurante.persistence.document.CatalogoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CatalogoRepositoryMongo extends MongoRepository<CatalogoDocument, String> {
    Optional<CatalogoDocument> findByIdPlato(Long idPlato);
}
