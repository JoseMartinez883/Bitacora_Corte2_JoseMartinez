package com.restaurante.persistence.document;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@Document(collection = "eventos_pedido")
public class EventoPedidoDocument {

    @Id
    private String id; // Mongo autogenera un UUID veloz
    
    private Long idPedido;
    private String estadoAnterior;
    private String estadoNuevo;
    private String usuarioQueCambio;
    private LocalDateTime timestamp;
}
