package com.restaurante.controller;

import com.restaurante.model.dto.LoginRequestDTO;
import com.restaurante.model.dto.TokenResponseDTO;
import com.restaurante.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final com.restaurante.repository.UsuarioRepositoryJPA usuarioRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {

        // 1. Validar credenciales con Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2. Extraer el usuario validado
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 3. Obtener el rol para inyectarlo en el JWT
        String rol = userDetails.getAuthorities().stream()
                .findFirst()
                .map(auth -> auth.getAuthority())
                .orElse("UNKNOWN");

        // 4. Generar el JWT
        String token = jwtUtil.generateToken(userDetails.getUsername(), rol);

        // 5. Retornar el DTO con el token
        return ResponseEntity.ok(new TokenResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody com.restaurante.model.dto.RegisterRequestDTO request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("El email ya está registrado");
        }

        com.restaurante.persistence.entity.UsuarioEntity newUser = com.restaurante.persistence.entity.UsuarioEntity.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(request.getRol())
                .build();

        usuarioRepository.save(newUser);
        return ResponseEntity.ok("Usuario registrado exitosamente");
    }
}
