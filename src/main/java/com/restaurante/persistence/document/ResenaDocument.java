package com.restaurante.persistence.document;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@Document(collection = "resenas")
public class ResenaDocument {
    @Id
    private String id;
    private Long idPedido; // Relación lógica con PostgreSQL
    private String nombreCliente;
    private int calificacion; // 1 a 5 estrellas
    private String comentario;
    private LocalDateTime fechaCreacion;
}
