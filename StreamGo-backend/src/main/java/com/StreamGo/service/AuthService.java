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

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // REGISTRO
    public AuthResponse register(RegisterRequest request) {

        // Validar si el email ya existe
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        // Crear Usuario
        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(Rol.CLIENTE)
                .estado(EstadoUsuario.ACTIVO)
                .fechaRegistro(LocalDateTime.now())
                .build();

        // Guardar en BD
        usuarioRepository.save(usuario);

        // Generar token JWT
        String token = jwtService.generateToken(usuario);

        return AuthResponse.builder()
                .token(token)
                .mensaje("Usuario registrado correctamente")
                .build();
    }


    // LOGIN
    public AuthResponse login(LoginRequest request) {

        // Buscar Usuario
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar password
        boolean passwordValida = passwordEncoder.matches(
                request.getPassword(),
                usuario.getPassword()
        );

        if (!passwordValida) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // ACTUALIZAR ÚLTIMO ACCESO
        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(usuario);


        // Generar token
        String token = jwtService.generateToken(usuario);

        return AuthResponse.builder()
                .token(token)
                .mensaje("Login exitoso")
                .build();
    }
}