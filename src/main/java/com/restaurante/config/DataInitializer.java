package com.restaurante.config;

import com.restaurante.persistence.entity.UsuarioEntity;
import com.restaurante.repository.UsuarioRepositoryJPA;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UsuarioRepositoryJPA usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            seedUser(usuarioRepository, passwordEncoder, "admin@restaurante.com", "admin123", "ADMIN");
            seedUser(usuarioRepository, passwordEncoder, "chef@restaurante.com", "chef123", "CHEF");
            seedUser(usuarioRepository, passwordEncoder, "mesero@restaurante.com", "mesero123", "MESERO");
            seedUser(usuarioRepository, passwordEncoder, "cliente@restaurante.com", "cliente123", "CLIENTE");
            System.out.println("Base de datos sincronizada con usuarios por defecto.");
        };
    }

    private void seedUser(UsuarioRepositoryJPA repository, PasswordEncoder encoder, String email, String password, String role) {
        repository.findByEmail(email).ifPresentOrElse(
                user -> {
                    user.setPassword(encoder.encode(password));
                    user.setRol(role);
                    repository.save(user);
                },
                () -> {
                    UsuarioEntity newUser = UsuarioEntity.builder()
                            .email(email)
                            .password(encoder.encode(password))
                            .rol(role)
                            .build();
                    repository.save(newUser);
                }
        );
    }
}
