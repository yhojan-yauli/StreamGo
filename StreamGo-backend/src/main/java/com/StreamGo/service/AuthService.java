package com.StreamGo.service;

import com.StreamGo.dto.response.AuthResponse;
import com.StreamGo.dto.request.LoginRequest;
import com.StreamGo.dto.request.RegisterRequest;
import com.StreamGo.entity.Enum.EstadoUsuario;
import com.StreamGo.entity.Enum.Rol;
import com.StreamGo.entity.Usuario;
import com.StreamGo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de autenticación del sistema StreamGo.
 * Maneja registro, login y generación de tokens JWT.
 *
 * @author Yhojan Yauli
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Registra un nuevo usuario en el sistema.
     * Valida que el correo no exista y genera un token JWT.
     *
     * @param request datos del usuario a registrar
     * @return respuesta con token JWT y mensaje de confirmación
     */
    public AuthResponse register(RegisterRequest request) {
        log.info("Intentando registrar usuario con email: {}", request.getEmail());

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            log.warn("Intento de registro con correo ya existente: {}", request.getEmail());
            throw new RuntimeException("El correo ya está registrado");
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(Rol.CLIENTE)
                .estado(EstadoUsuario.INACTIVO)
                .fechaRegistro(LocalDateTime.now())
                .build();

        usuarioRepository.save(usuario);
        log.info("Usuario registrado correctamente: {}", usuario.getEmail());

        String token = jwtService.generateToken(usuario);

        return AuthResponse.builder()
                .token(token)
                .mensaje("Usuario registrado correctamente")
                .build();
    }

    /**
     * Autentica un usuario en el sistema.
     * Valida credenciales y genera token JWT.
     *
     * @param request credenciales de inicio de sesión
     * @return respuesta con token JWT y mensaje de éxito
     */
    public AuthResponse login(LoginRequest request) {
        log.info("Intento de login para {}", request.getEmail());

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login fallido. Usuario no encontrado: {}", request.getEmail());
                    return new RuntimeException("Usuario no encontrado");
                });

        boolean passwordValida = passwordEncoder.matches(
                request.getPassword(),
                usuario.getPassword()
        );

        if (!passwordValida) {
            log.warn("Login fallido. Contraseña incorrecta para {}", request.getEmail());
            throw new RuntimeException("Contraseña incorrecta");
        }

        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(usuario);
        log.info("Login exitoso para {}", usuario.getEmail());

        String token = jwtService.generateToken(usuario);

        return AuthResponse.builder()
                .token(token)
                .mensaje("Login exitoso")
                .build();
    }

    /**
     * Registra un nuevo usuario en el sistema a través de una cuenta de Google (OAuth2).
     *
     * @param email email obtenido de Google
     * @param nombre nombre obtenido de Google
     * @return El usuario guardado en la base de datos
     */
    public Usuario registerFromGoogle(String email, String nombre, String avatar) {
        log.info("Intentando registrar usuario vía Google con email: {}", email);

        if (usuarioRepository.existsByEmail(email)) {
            log.warn("Intento de registro vía Google con correo ya existente: {}", email);
            throw new RuntimeException("El correo ya está registrado");
        }

        Usuario usuario = Usuario.builder()
                .nombre(nombre)
                .email(email)
                .avatar(avatar)
                .password("") // Al ser login social, no maneja contraseña clásica
                .rol(Rol.CLIENTE)
                .estado(EstadoUsuario.INACTIVO) // Mantiene tu regla de negocio
                .fechaRegistro(LocalDateTime.now())
                .ultimoAcceso(LocalDateTime.now())
                .build();

        log.info("Usuario registrado correctamente vía Google: {}", email);
        return usuarioRepository.save(usuario);
    }
}