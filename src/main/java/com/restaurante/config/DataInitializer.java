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
            if (usuarioRepository.count() == 0) {
                UsuarioEntity admin = UsuarioEntity.builder()
                        .email("admin@restaurante.com")
                        .password(passwordEncoder.encode("admin123"))
                        .rol("ROLE_ADMIN")
                        .build();

                UsuarioEntity chef = UsuarioEntity.builder()
                        .email("chef@restaurante.com")
                        .password(passwordEncoder.encode("chef123"))
                        .rol("ROLE_CHEF")
                        .build();

                UsuarioEntity mesero = UsuarioEntity.builder()
                        .email("mesero@restaurante.com")
                        .password(passwordEncoder.encode("mesero123"))
                        .rol("ROLE_MESERO")
                        .build();

                UsuarioEntity cliente = UsuarioEntity.builder()
                        .email("cliente@restaurante.com")
                        .password(passwordEncoder.encode("cliente123"))
                        .rol("ROLE_CLIENTE")
                        .build();

                usuarioRepository.saveAll(List.of(admin, chef, mesero, cliente));
                System.out.println("Base de datos inicializada con usuarios por defecto.");
            }
        };
    }
}
