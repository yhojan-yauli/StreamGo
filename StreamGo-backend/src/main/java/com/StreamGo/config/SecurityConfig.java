package com.StreamGo.config;

import com.StreamGo.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Configuración principal de seguridad
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Desactivar CSRF para APIs REST
                .csrf(csrf -> csrf.disable())

                // JWT sin sesiones
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Configuración de rutas
                .authorizeHttpRequests(auth -> auth

                        // Webhooks públicos
                        .requestMatchers("/webhook/**")
                        .permitAll()

                        // Auth y Swagger públicos
                        .requestMatchers(
                                "/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Contenido público SIN LOGIN
                        .requestMatchers("/public/**")
                        .permitAll()

                        // Rutas ADMIN
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        // Catálogo para usuarios logueados
                        .requestMatchers("/contenidos/**")
                        .hasAnyRole("CLIENTE", "ADMIN")

                        // Reproducción para usuarios logueados
                        .requestMatchers("/reproduccion/**")
                        .hasRole("CLIENTE")

                        // Otras rutas requieren login
                        .anyRequest().authenticated()
                )

                // Filtro JWT
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // Encriptador BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Authentication Manager
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {

        return config.getAuthenticationManager();
    }
}