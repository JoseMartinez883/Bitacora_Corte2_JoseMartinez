package com.restaurante.config;

import com.restaurante.persistence.entity.UsuarioEntity;
import com.restaurante.repository.UsuarioRepositoryJPA;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UsuarioRepositoryJPA usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            seedUser(usuarioRepository, passwordEncoder, "admin@restaurante.com", "admin123", "ADMIN");
            seedUser(usuarioRepository, passwordEncoder, "chef@restaurante.com", "chef123", "CHEF");
            seedUser(usuarioRepository, passwordEncoder, "mesero@restaurante.com", "mesero123", "MESERO");
            seedUser(usuarioRepository, passwordEncoder, "cliente@restaurante.com", "cliente123", "CLIENTE");
            log.info("Base de datos sincronizada con usuarios por defecto.");
        };
    }

    private void seedUser(UsuarioRepositoryJPA repository, PasswordEncoder encoder, String email, String password, String role) {
        if (repository.findByEmail(email).isEmpty()) {
            UsuarioEntity newUser = UsuarioEntity.builder()
                    .email(email)
                    .password(encoder.encode(password))
                    .rol(role)
                    .build();
            repository.save(newUser);
            log.info("Usuario por defecto creado exitosamente: {} ({})", email, role);
        } else {
            log.debug("Usuario {} ya existe en la base de datos, omitiendo seed.", email);
        }
    }
}
