package com.restaurante.service;

import com.restaurante.model.dto.RegisterRequestDTO;
import com.restaurante.persistence.entity.UsuarioEntity;
import com.restaurante.repository.UsuarioRepositoryJPA;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepositoryJPA usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void registrarCliente(RegisterRequestDTO request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("Registro rechazado: email '{}' ya está registrado", request.getEmail());
            throw new IllegalArgumentException("El email ya está registrado");
        }

        UsuarioEntity newUser = UsuarioEntity.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol("CLIENTE")
                .build();

        usuarioRepository.save(newUser);
        log.info("Cliente '{}' registrado exitosamente", request.getEmail());
    }

    @Override
    @Transactional
    public void registrarEmpleado(RegisterRequestDTO request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("Registro de empleado rechazado: email '{}' ya está registrado", request.getEmail());
            throw new IllegalArgumentException("El email ya está registrado");
        }

        String rol = (request.getRol() != null && !request.getRol().isBlank()) ? request.getRol().toUpperCase() : "MESERO";
        if (rol.startsWith("ROLE_")) {
            rol = rol.substring(5);
        }

        UsuarioEntity newUser = UsuarioEntity.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(rol)
                .build();

        usuarioRepository.save(newUser);
        log.info("Empleado '{}' registrado exitosamente con rol '{}'", request.getEmail(), rol);
    }
}

