package com.restaurante.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    
    private final String testSecret = "TWlTdXBlclNlY3JldG9TZWd1cm9QYXJhUHJ1ZWJhczEyMzQ1Njc4OTA9";
    private final Long testExpirationMs = 3600000L; // 1 hora

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Usamos ReflectionTestUtils para inyectar las propiedades @Value sin levantar todo el contexto de Spring
        ReflectionTestUtils.setField(jwtUtil, "secret", testSecret);
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", testExpirationMs);
    }

    @Test
    void testGenerateToken_And_ExtractClaims() {
        // Arrange
        String email = "admin@bellaciao.com";
        String rol = "ROLE_ADMIN";

        // Act
        String token = jwtUtil.generateToken(email, rol);
        Claims claims = jwtUtil.extractClaims(token);

        // Assert
        assertNotNull(token);
        assertEquals(email, claims.getSubject());
        assertEquals(rol, claims.get("rol"));
    }

    @Test
    void testIsValid_WithValidToken() {
        // Arrange
        String token = jwtUtil.generateToken("user@test.com", "ROLE_CLIENTE");

        // Act
        boolean isValid = jwtUtil.isValid(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void testIsValid_WithInvalidToken() {
        // Arrange
        String invalidToken = "este.no.es.un.token.valido";

        // Act
        boolean isValid = jwtUtil.isValid(invalidToken);

        // Assert
        assertFalse(isValid);
    }
    
    @Test
    void testIsValid_WithNullToken() {
        // Act
        boolean isValid = jwtUtil.isValid(null);

        // Assert
        assertFalse(isValid);
    }
}
