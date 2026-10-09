package com.restaurante.security;

import com.restaurante.persistence.entity.UsuarioEntity;
import com.restaurante.repository.UsuarioRepositoryJPA;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CustomOAuth2SuccessHandlerTest {

    @Mock
    private UsuarioRepositoryJPA usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private CustomOAuth2SuccessHandler successHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void onAuthenticationSuccess_usuarioExistente() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Authentication authentication = mock(Authentication.class);
        OAuth2User oauthUser = mock(OAuth2User.class);

        when(authentication.getPrincipal()).thenReturn(oauthUser);
        when(oauthUser.getAttribute("email")).thenReturn("existente@test.com");
        when(oauthUser.getAttribute("name")).thenReturn("Test User");

        UsuarioEntity user = new UsuarioEntity();
        user.setEmail("existente@test.com");
        user.setRol("CLIENTE");
        when(usuarioRepository.findByEmail("existente@test.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("existente@test.com", "CLIENTE")).thenReturn("token123");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        successHandler.onAuthenticationSuccess(request, response, authentication);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        assertTrue(stringWriter.toString().contains("token123"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void onAuthenticationSuccess_usuarioNuevo() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Authentication authentication = mock(Authentication.class);
        OAuth2User oauthUser = mock(OAuth2User.class);

        when(authentication.getPrincipal()).thenReturn(oauthUser);
        when(oauthUser.getAttribute("email")).thenReturn("nuevo@test.com");
        when(oauthUser.getAttribute("name")).thenReturn("Nuevo User");

        when(usuarioRepository.findByEmail("nuevo@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");

        UsuarioEntity newUser = new UsuarioEntity();
        newUser.setEmail("nuevo@test.com");
        newUser.setRol("CLIENTE");
        
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(newUser);
        when(jwtUtil.generateToken("nuevo@test.com", "CLIENTE")).thenReturn("token123");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        successHandler.onAuthenticationSuccess(request, response, authentication);

        assertTrue(stringWriter.toString().contains("token123"));
        verify(usuarioRepository).save(any(UsuarioEntity.class));
    }
}
