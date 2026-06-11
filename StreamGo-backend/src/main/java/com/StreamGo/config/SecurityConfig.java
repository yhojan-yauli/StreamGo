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

import com.StreamGo.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

/**
 * Configuración principal de seguridad del backend de StreamGo.
 *
 * Define las rutas públicas, rutas protegidas por rol,
 * autenticación mediante JWT y política sin sesiones.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configura la cadena de filtros de seguridad de Spring Security.
     *
     * Permite rutas públicas como autenticación, Swagger, noticias públicas
     * y reproducción pública. También protege rutas administrativas,
     * rutas de cliente, reproducción, calificaciones e historial.
     *
     * @param http objeto de configuración de seguridad HTTP.
     * @return cadena de filtros de seguridad configurada.
     * @throws Exception si ocurre un error durante la configuración.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Desactivar CSRF para APIs REST
                .csrf(csrf -> csrf.disable())

                // Habilitar configuración CORS básica
                .cors(cors -> {})

                // JWT sin sesiones
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Configuración de rutas
                .authorizeHttpRequests(auth -> auth

                        // Webhooks públicos
                        .requestMatchers("/webhook/**").permitAll()

                        // Auth y Swagger públicos
                        .requestMatchers(
                                "/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Rutas públicas sin autenticación
                        .requestMatchers("/public/**").permitAll()

                        // Noticias públicas y administración de noticias
                        .requestMatchers(HttpMethod.GET, "/noticias/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/noticias/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/noticias/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/noticias/**").hasAnyRole("ADMIN", "CLIENTE")
                        .requestMatchers(HttpMethod.DELETE, "/noticias/**").hasRole("ADMIN")

                        // Rutas administrativas
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Catálogo de contenidos para clientes y administradores
                        .requestMatchers("/contenidos/**").hasAnyRole("CLIENTE", "ADMIN")

                        // Rutas exclusivas para clientes
                        .requestMatchers("/reproduccion/**").hasRole("CLIENTE")
                        .requestMatchers("/calificaciones/**").hasRole("CLIENTE")
                        .requestMatchers("/historial/**").hasRole("CLIENTE")

                        // Cualquier otra ruta requiere autenticación
                        .anyRequest().authenticated()
                )

                // Filtro JWT antes del filtro de autenticación tradicional
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    /**
     * Define el encriptador de contraseñas utilizado por el sistema.
     *
     * @return instancia de BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Expone el AuthenticationManager de Spring Security.
     *
     * @param config configuración de autenticación.
     * @return AuthenticationManager configurado.
     * @throws Exception si ocurre un error al obtener el manager.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }
}
