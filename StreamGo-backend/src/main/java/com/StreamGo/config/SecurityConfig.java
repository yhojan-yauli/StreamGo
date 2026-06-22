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

                // Enlazar la configuración de CORS nativa de Spring Security
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

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

                        // Permisos de Noticias
                        .requestMatchers(HttpMethod.GET, "/noticias/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/noticias/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/noticias/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/noticias/**").hasAnyAuthority("ADMIN", "CLIENTE")
                        .requestMatchers(HttpMethod.DELETE, "/noticias/**").hasAuthority("ADMIN")

                        // Rutas ADMIN (Manejo estricto de autoridades sin prefijos)
                        .requestMatchers(HttpMethod.GET, "/admin/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/admin/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/admin/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/admin/**").hasAuthority("ADMIN")

                        // Catálogo de contenidos
                        .requestMatchers("/contenidos/**").hasAnyAuthority("CLIENTE", "ADMIN")

                        // Reproducción
                        .requestMatchers("/reproduccion/**").hasAuthority("CLIENTE")

                        // Calificaciones solo para clientes
                        .requestMatchers("/calificaciones/**").hasAuthority("CLIENTE")

                        // Historial solo para clientes
                        .requestMatchers("/historial/**").hasAuthority("CLIENTE")

                        // Otras rutas requieren estar autenticados
                        .anyRequest().authenticated()
                )

                // Filtro JWT antes del filtro de usuario/contraseña
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // Configuración explícita de CORS para Spring Security
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
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