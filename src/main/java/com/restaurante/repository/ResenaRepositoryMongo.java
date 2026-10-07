package com.restaurante.repository;

import com.restaurante.persistence.document.ResenaDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResenaRepositoryMongo extends MongoRepository<ResenaDocument, String> {
    List<ResenaDocument> findByCalificacionGreaterThanEqual(int calificacion);
}
