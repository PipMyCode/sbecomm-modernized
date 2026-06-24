package com.sbecomm.modernized.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final KeycloakJwtAuthenticationConverter keycloakJwtAuthenticationConverter;

    public SecurityConfig(KeycloakJwtAuthenticationConverter keycloakJwtAuthenticationConverter) {
        this.keycloakJwtAuthenticationConverter = keycloakJwtAuthenticationConverter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Completely Stateless (No Sessions)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 2. Disable CSRF because this is a stateless REST API (no cookies used for auth)
            .csrf(AbstractHttpConfigurer::disable)
            
            // 3. Define Endpoint Rules (Method level handles the rest)
            .authorizeHttpRequests(auth -> auth
                // Allow public access to Swagger UI and API Docs
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                // Allow public read access to the catalog
                .requestMatchers(HttpMethod.GET, "/api/v1/catalog/categories").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/catalog/products/**").permitAll()
                // Require authentication for all other endpoints
                .anyRequest().authenticated()
            )
            
            // 4. Configure OAuth2 Resource Server to use our custom Keycloak JWT converter
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtAuthenticationConverter))
            );

        return http.build();
    }
}
