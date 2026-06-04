package com.StreamGo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;

import com.StreamGo.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

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
                .cors(cors -> {})
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
                        
                        // Permisos de Noticias
                        .requestMatchers(HttpMethod.GET, "/noticias/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/noticias/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/noticias/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/noticias/**").hasAnyRole("ADMIN", "CLIENTE")
                        .requestMatchers(HttpMethod.DELETE, "/noticias/**").hasRole("ADMIN")
                                        
                        // Rutas ADMIN
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Catálogo de contenidos
                        .requestMatchers("/contenidos/**").hasAnyRole("CLIENTE", "ADMIN")

                        // Reproducción
                        .requestMatchers("/reproduccion/**").hasRole("CLIENTE")

                        // Calificaciones solo para clientes
                        .requestMatchers("/calificaciones/**")
                        .hasRole("CLIENTE")

                        // Historial solo para clientes para la parte de mis listas del frontend
                        .requestMatchers("/historial/**")
                        .hasRole("CLIENTE")

                        // Calificaciones solo para clientes
                        .requestMatchers("/calificaciones/**")
                        .hasRole("CLIENTE")

                        // Historial solo para clientes para la parte de mis listas del frontend
                        .requestMatchers("/historial/**")
                        .hasRole("CLIENTE")

                                                // Otras rutas requieren login
                        .anyRequest().authenticated()
                )
                
                // Filtro JWT antes del filtro de usuario/contraseña
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