package com.restaurante.service;

import com.restaurante.model.dto.RegisterRequestDTO;
import com.restaurante.persistence.entity.UsuarioEntity;
import com.restaurante.repository.UsuarioRepositoryJPA;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepositoryJPA usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Test
    void registrarCliente_exitoso() {
        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setEmail("nuevo@cliente.com");
        req.setPassword("pass123");

        when(usuarioRepository.findByEmail("nuevo@cliente.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass123")).thenReturn("hash123");
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenAnswer(i -> i.getArgument(0));

        assertDoesNotThrow(() -> usuarioService.registrarCliente(req));
        verify(usuarioRepository).save(any(UsuarioEntity.class));
    }

    @Test
    void registrarCliente_fallaSiEmailYaExiste() {
        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setEmail("existe@cliente.com");
        req.setPassword("pass123");

        when(usuarioRepository.findByEmail("existe@cliente.com")).thenReturn(Optional.of(new UsuarioEntity()));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> usuarioService.registrarCliente(req));
        assertEquals("El email ya está registrado", ex.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void registrarEmpleado_exitosoConRol() {
        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setEmail("nuevo@chef.com");
        req.setPassword("pass123");
        req.setRol("ROLE_CHEF");

        when(usuarioRepository.findByEmail("nuevo@chef.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass123")).thenReturn("hash123");
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenAnswer(i -> i.getArgument(0));

        assertDoesNotThrow(() -> usuarioService.registrarEmpleado(req));
        verify(usuarioRepository).save(argThat(u -> "CHEF".equals(u.getRol())));
    }

    @Test
    void registrarEmpleado_defaultMeseroSiRolVacio() {
        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setEmail("nuevo@mesero.com");
        req.setPassword("pass123");
        req.setRol("");

        when(usuarioRepository.findByEmail("nuevo@mesero.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass123")).thenReturn("hash123");
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenAnswer(i -> i.getArgument(0));

        assertDoesNotThrow(() -> usuarioService.registrarEmpleado(req));
        verify(usuarioRepository).save(argThat(u -> "MESERO".equals(u.getRol())));
    }

    @Test
    void registrarEmpleado_fallaSiEmailYaExiste() {
        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setEmail("existe@empleado.com");
        req.setPassword("pass123");

        when(usuarioRepository.findByEmail("existe@empleado.com")).thenReturn(Optional.of(new UsuarioEntity()));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> usuarioService.registrarEmpleado(req));
        assertEquals("El email ya está registrado", ex.getMessage());
        verify(usuarioRepository, never()).save(any());
    }
}

