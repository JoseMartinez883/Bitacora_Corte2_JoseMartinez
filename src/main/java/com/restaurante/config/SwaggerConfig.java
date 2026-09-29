package com.restaurante.config;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API Bella Ciao - Restaurante",
                version = "v1.0",
                description = "API REST para gestión operativa de restaurante.",
                contact = @Contact(name = "Equipo DOSW", email = "jose.martinez@mail.escuelaing.edu.co")
        )
)
public class SwaggerConfig { }

