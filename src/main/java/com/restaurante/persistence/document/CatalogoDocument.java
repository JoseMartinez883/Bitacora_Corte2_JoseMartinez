package com.restaurante.persistence.document;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@Document(collection = "catalogos")
public class CatalogoDocument {
    @Id
    private String id;
    private Long idPlato; // Relación lógica con PostgreSQL
    private List<String> imagenesUrls;
    private List<String> etiquetasComerciales;
}
