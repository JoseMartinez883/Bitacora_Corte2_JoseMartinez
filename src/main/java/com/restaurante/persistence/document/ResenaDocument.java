package com.restaurante.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "resenas")
public class ResenaDocument {
    @Id
    private String id;
    @Indexed(unique = true)
    private UUID idPedido; // Relación lógica con PostgreSQL (una reseña por pedido)
    private String emailUsuario; // Autor tomado del token JWT, no expuesto en la respuesta
    private String nombreCliente;
    private int calificacion; // 1 a 5 estrellas
    private String comentario;
    private LocalDateTime fechaCreacion;
}
