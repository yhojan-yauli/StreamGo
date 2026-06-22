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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.StreamGo.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import java.util.Arrays;

/**
 * Configuración principal de seguridad del backend de StreamGo.
 *
 * Define las rutas públicas, rutas protegidas por autoridad,
 * autenticación mediante JWT, configuración CORS y política sin sesiones.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configura la cadena de filtros de seguridad de Spring Security.
     *
     * @param http objeto de configuración HTTP.
     * @return cadena de filtros configurada.
     * @throws Exception si ocurre un error durante la configuración.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth

                        // Rutas públicas generales
                        .requestMatchers("/webhook/**").permitAll()
                        .requestMatchers(
                                "/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Rutas públicas del frontend: contenidos y reproducción SINLOGIN
                        .requestMatchers("/public/**").permitAll()

                        // Planes públicos
                        .requestMatchers(HttpMethod.GET, "/planes/**").permitAll()

                        // Noticias públicas y administración de noticias
                        .requestMatchers(HttpMethod.GET, "/noticias/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/noticias/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/noticias/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/noticias/**").hasAnyAuthority("ADMIN", "CLIENTE")
                        .requestMatchers(HttpMethod.DELETE, "/noticias/**").hasAuthority("ADMIN")

                        // Administración
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")

                        // Rutas de cliente
                        .requestMatchers("/cliente/**").hasAuthority("CLIENTE")
                        .requestMatchers("/payments/**").hasAuthority("CLIENTE")
                        .requestMatchers("/reproduccion/**").hasAuthority("CLIENTE")
                        .requestMatchers("/calificaciones/**").hasAuthority("CLIENTE")
                        .requestMatchers("/historial/**").hasAuthority("CLIENTE")

                        // Catálogo autenticado para cliente o administrador
                        .requestMatchers("/contenidos/**").hasAnyAuthority("CLIENTE", "ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    /**
     * Configura CORS para permitir el consumo desde Angular y Live Server.
     *
     * @return fuente de configuración CORS.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:4200",
                "http://localhost:5500",
                "http://127.0.0.1:5500"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Define el encriptador de contraseñas del sistema.
     *
     * @return PasswordEncoder con BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Expone el AuthenticationManager usado por Spring Security.
     *
     * @param config configuración de autenticación.
     * @return AuthenticationManager configurado.
     * @throws Exception si ocurre un error al obtenerlo.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }
}
