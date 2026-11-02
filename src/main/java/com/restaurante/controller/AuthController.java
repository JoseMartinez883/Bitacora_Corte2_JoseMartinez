package com.restaurante.controller;

import com.restaurante.model.dto.LoginRequestDTO;
import com.restaurante.model.dto.RegisterRequestDTO;
import com.restaurante.model.dto.TokenResponseDTO;
import com.restaurante.security.JwtUtil;
import com.restaurante.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping({"/api/v1/auth", "/auth", "/api/auth"})
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para inicio de sesión y registro de usuarios con JWT")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;

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
            return ResponseEntity.status(401).body(Map.of(
                    "error", "Unauthorized",
                    "message", "Credenciales invalidas"
            ));
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario", description = "Crea un usuario con rol CLIENTE mediante el servicio de usuarios.")
    @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente")
    @ApiResponse(responseCode = "400", description = "El correo ya está registrado o datos inválidos")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDTO request) {
        try {
            usuarioService.registrarCliente(request);
            return ResponseEntity.ok("Usuario registrado exitosamente");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/empleados")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Registrar empleado", description = "Exclusivo para administradores. Permite registrar personal con rol ADMIN, CHEF o MESERO.")
    @ApiResponse(responseCode = "200", description = "Empleado registrado exitosamente")
    @ApiResponse(responseCode = "400", description = "El correo ya está registrado o datos inválidos")
    @ApiResponse(responseCode = "403", description = "Acceso denegado: requiere rol ADMIN")
    public ResponseEntity<String> registerEmpleado(@Valid @RequestBody RegisterRequestDTO request) {
        try {
            usuarioService.registrarEmpleado(request);
            String rol = (request.getRol() != null && !request.getRol().isBlank()) ? request.getRol().toUpperCase() : "MESERO";
            if (rol.startsWith("ROLE_")) {
                rol = rol.substring(5);
            }
            return ResponseEntity.ok("Empleado con rol " + rol + " registrado exitosamente");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
