package com.restaurante.controller;

import com.restaurante.model.dto.LoginRequestDTO;
import com.restaurante.model.dto.TokenResponseDTO;
import com.restaurante.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping({"/auth", "/api/auth", "/api/v1/auth"})
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para inicio de sesión y registro de usuarios con JWT")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final com.restaurante.repository.UsuarioRepositoryJPA usuarioRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica credenciales y genera un token JWT para autorizar peticiones.")
    @ApiResponse(responseCode = "200", description = "Autenticación exitosa, token JWT retornado")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequestDTO request) {
        try {
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
                    .orElse("ROLE_CLIENTE");

            // 4. Generar el JWT
            String token = jwtUtil.generateToken(userDetails.getUsername(), rol);

            // 5. Retornar el DTO con el token
            return ResponseEntity.ok(new TokenResponseDTO(token));
        } catch (org.springframework.security.core.AuthenticationException ex) {
            return ResponseEntity.status(401).body(java.util.Map.of(
                    "error", "Unauthorized",
                    "message", "Credenciales invalidas"
            ));
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario", description = "Crea un usuario con contraseña cifrada mediante BCrypt.")
    @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente")
    @ApiResponse(responseCode = "400", description = "El correo ya está registrado o datos inválidos")
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
