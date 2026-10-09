package com.restaurante.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String APPLICATION_JSON = "application/json";

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomOAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @SuppressWarnings("java:S4502") // Disabling CSRF is safe for stateless REST APIs
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())   // usa el bean CorsConfigurationSource de CorsConfig
            .csrf(AbstractHttpConfigurer::disable)
            .headers(headers -> headers
                .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'; script-src 'self'"))
                .frameOptions(frame -> frame.deny())
                .xssProtection(xss -> xss.headerValue(org.springframework.security.web.header.writers.XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
            )
            // .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Comentado para permitir el handshake de OAuth2
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/error").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/menu").permitAll()
                .requestMatchers(HttpMethod.POST,
                    "/auth/login", "/auth/register",
                    "/api/auth/login", "/api/auth/register",
                    "/api/v1/auth/login", "/api/v1/auth/register"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .successHandler(oAuth2SuccessHandler)
                .failureHandler((request, response, exception) -> {
                    log.error("Error en OAuth2: ", exception);
                    response.setContentType(APPLICATION_JSON);
                    response.setStatus(401);
                    response.getWriter().write("{\"error\": \"OAuth2 Failed\", \"message\": \"" + exception.getMessage() + "\"}");
                })
            )
            .httpBasic(Customizer.withDefaults())   // HTTP Basic (sobre HTTPS) conviviendo con JWT
            .exceptionHandling(exc -> exc
                .authenticationEntryPoint((request, response, authException) -> {
                    log.warn("Acceso no autenticado [401] a {} desde {}", request.getRequestURI(), request.getRemoteAddr());
                    response.setContentType(APPLICATION_JSON);
                    response.setStatus(401);
                    response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Falta token o es invalido\"}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    log.warn("Acceso denegado [403] a {} desde {}", request.getRequestURI(), request.getRemoteAddr());
                    response.setContentType(APPLICATION_JSON);
                    response.setStatus(403);
                    response.getWriter().write("{\"error\": \"Forbidden\", \"message\": \"No tienes permisos para esta accion\"}");
                })
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
