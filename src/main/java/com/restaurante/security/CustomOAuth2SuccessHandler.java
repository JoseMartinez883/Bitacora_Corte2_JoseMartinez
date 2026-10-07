package com.restaurante.security;

import com.restaurante.persistence.entity.UsuarioEntity;
import com.restaurante.repository.UsuarioRepositoryJPA;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UsuarioRepositoryJPA usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String nombre = oauthUser.getAttribute("name");

        log.info("Login OAuth2 exitoso desde Google para el usuario: {} ({})", email, nombre);

        // Buscar el usuario o registrarlo si no existe
        UsuarioEntity usuario = usuarioRepository.findByEmail(email).orElseGet(() -> {
            log.info("Registrando nuevo usuario OAuth2: {}", email);
            UsuarioEntity newUser = UsuarioEntity.builder()
                    .email(email)
                    .password(passwordEncoder.encode(UUID.randomUUID().toString())) // Contraseña aleatoria segura
                    .rol("ROLE_CLIENTE") // Por defecto Cliente
                    .build();
            return usuarioRepository.save(newUser);
        });

        // Generar JWT
        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getRol());
        
        // En una API REST, devolver el token. En OAuth clásico se devuelve en la URL o JSON.
        // Como es solo Backend, devolvemos el token en un JSON.
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"token\": \"" + token + "\", \"message\": \"Login OAuth2 exitoso\"}");
    }
}
