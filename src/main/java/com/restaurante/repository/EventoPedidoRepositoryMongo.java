package com.restaurante.repository;

import com.restaurante.persistence.document.EventoPedidoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventoPedidoRepositoryMongo extends MongoRepository<EventoPedidoDocument, String> {
    List<EventoPedidoDocument> findByIdPedidoOrderByTimestampDesc(Long idPedido);
}
