package com.restaurante.security;

import com.restaurante.service.UsuarioDetailsService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UsuarioDetailsService usuarioDetailsService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        // Limpiamos el contexto antes de cada prueba para que no haya falsos positivos
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        // Limpiamos el contexto después de cada prueba
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_WithValidJwt_ShouldSetAuthentication() throws ServletException, IOException {
        // Arrange
        String token = "valid.jwt.token";
        String email = "admin@bellaciao.com";
        request.addHeader("Authorization", "Bearer " + token);

        when(jwtUtil.isValid(token)).thenReturn(true);
        
        Claims mockClaims = mock(Claims.class);
        when(mockClaims.getSubject()).thenReturn(email);
        when(jwtUtil.extractClaims(token)).thenReturn(mockClaims);

        UserDetails userDetails = new User(email, "password", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(usuarioDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        // Comprobamos que el filtro haya inyectado exitosamente el usuario en el contexto de Spring Security
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(email, SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithoutAuthorizationHeader_ShouldContinueChainWithoutAuth() throws ServletException, IOException {
        // Arrange
        // No añadimos el header Authorization a la petición
        
        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil);
        verifyNoInteractions(usuarioDetailsService);
    }

    @Test
    void doFilterInternal_WithInvalidJwt_ShouldContinueChainWithoutAuth() throws ServletException, IOException {
        // Arrange
        String token = "invalid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);

        when(jwtUtil.isValid(token)).thenReturn(false);

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).extractClaims(anyString());
        verifyNoInteractions(usuarioDetailsService);
    }
}
