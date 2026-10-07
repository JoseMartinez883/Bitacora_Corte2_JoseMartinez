package com.restaurante.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.model.dto.LoginRequestDTO;
import com.restaurante.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@org.springframework.context.annotation.Import({com.restaurante.security.SecurityConfig.class, com.restaurante.security.JwtAuthFilter.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private com.restaurante.service.UsuarioDetailsService usuarioDetailsService;

    @MockitoBean
    private com.restaurante.repository.UsuarioRepositoryJPA usuarioRepository;

    @MockitoBean
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testLogin_Success() throws Exception {
        // Arrange
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("admin@bellaciao.com");
        loginRequest.setPassword("123456");

        // Creamos un UserDetails de prueba que simule la validación exitosa
        UserDetails mockUserDetails = new User(
                "admin@bellaciao.com", 
                "123456", 
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        
        Authentication auth = new UsernamePasswordAuthenticationToken(
                mockUserDetails, 
                null, 
                mockUserDetails.getAuthorities()
        );
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(jwtUtil.generateToken("admin@bellaciao.com", "ROLE_ADMIN")).thenReturn("fake-jwt-token");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken("admin@bellaciao.com", "ROLE_ADMIN");
    }

    @Test
    void testLogin_ValidationFailure() throws Exception {
        // Arrange
        LoginRequestDTO badRequest = new LoginRequestDTO(); // Email y password nulos
        
        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isBadRequest()); // Se espera que @Valid rechace la petición (400)
                
        // Verificamos que si el DTO viene mal, la lógica de negocio NUNCA se ejecute
        verifyNoInteractions(authenticationManager);
        verifyNoInteractions(jwtUtil);
    }
}
