package com.restaurante.service;

import com.restaurante.persistence.entity.UsuarioEntity;
import com.restaurante.repository.UsuarioRepositoryJPA;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioDetailsServiceTest {

    @Mock
    private UsuarioRepositoryJPA usuarioRepositoryJPA;

    @InjectMocks
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void testLoadUserByUsername_Success() {
        // Arrange
        String email = "admin@bellaciao.com";
        UsuarioEntity mockEntity = new UsuarioEntity();
        mockEntity.setId(1L);
        mockEntity.setEmail(email);
        mockEntity.setPassword("hashsecreto");
        mockEntity.setRol("ADMIN");

        when(usuarioRepositoryJPA.findByEmail(email)).thenReturn(Optional.of(mockEntity));

        // Act
        UserDetails userDetails = usuarioDetailsService.loadUserByUsername(email);

        // Assert
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals("hashsecreto", userDetails.getPassword());
        // Spring Security añade internamente el prefijo "ROLE_" cuando le pasas el rol "ADMIN" en el builder
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        verify(usuarioRepositoryJPA).findByEmail(email);
    }

    @Test
    void testLoadUserByUsername_RolePrefix() {
        String email = "mesero@bellaciao.com";
        UsuarioEntity mockEntity = new UsuarioEntity();
        mockEntity.setId(2L);
        mockEntity.setEmail(email);
        mockEntity.setPassword("hash");
        mockEntity.setRol("ROLE_MESERO"); // Probando con prefijo incluido
        when(usuarioRepositoryJPA.findByEmail(email)).thenReturn(Optional.of(mockEntity));

        UserDetails userDetails = usuarioDetailsService.loadUserByUsername(email);
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MESERO")));
    }

    @Test
    void testLoadUserByUsername_NullRole() {
        String email = "cliente@bellaciao.com";
        UsuarioEntity mockEntity = new UsuarioEntity();
        mockEntity.setId(3L);
        mockEntity.setEmail(email);
        mockEntity.setPassword("hash");
        mockEntity.setRol(null); // Rol nulo
        when(usuarioRepositoryJPA.findByEmail(email)).thenReturn(Optional.of(mockEntity));

        UserDetails userDetails = usuarioDetailsService.loadUserByUsername(email);
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE")));
    }

    @Test
    void testLoadUserByUsername_NotFound() {
        // Arrange
        String email = "noexiste@bellaciao.com";
        when(usuarioRepositoryJPA.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            usuarioDetailsService.loadUserByUsername(email);
        });
        
        assertEquals("Usuario no encontrado: " + email, exception.getMessage());
        verify(usuarioRepositoryJPA).findByEmail(email);
    }
}
