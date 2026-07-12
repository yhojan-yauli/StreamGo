package com.StreamGo.Auth;

import com.StreamGo.dto.request.LoginRequest;
import com.StreamGo.dto.request.RegisterRequest;
import com.StreamGo.dto.response.AuthResponse;
import com.StreamGo.entity.Enum.EstadoUsuario;
import com.StreamGo.entity.Enum.Rol;
import com.StreamGo.entity.Usuario;
import com.StreamGo.repository.UsuarioRepository;
import com.StreamGo.service.AuthService;
import com.StreamGo.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - AuthService")
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        usuarioMock = Usuario.builder()
                .id(1L)
                .nombre("Yhojan")
                .email("yhojan@streamgo.com")
                .password("passwordEncriptado")
                .rol(Rol.CLIENTE)
                .estado(EstadoUsuario.INACTIVO)
                .build();
    }

    @Nested
    @DisplayName("Escenarios de Registro (Register)")
    class RegisterTests {

        @Test
        @DisplayName("Debería registrar un usuario exitosamente")
        void register_Exito() {
            RegisterRequest request = new RegisterRequest();
            request.setNombre("Yhojan");
            request.setEmail("yhojan@streamgo.com");
            request.setPassword("123456");

            when(usuarioRepository.existsByEmail(request.getEmail())).thenReturn(false);
            when(passwordEncoder.encode(request.getPassword())).thenReturn("passwordEncriptado");
            when(jwtService.generateToken(any(Usuario.class))).thenReturn("mock-jwt-token");

            AuthResponse response = authService.register(request);

            assertNotNull(response);
            assertEquals("mock-jwt-token", response.getToken());
            assertEquals("Usuario registrado correctamente", response.getMensaje());
            verify(usuarioRepository, times(1)).save(any(Usuario.class));
        }

        @Test
        @DisplayName("Debería lanzar excepción si el correo ya existe")
        void register_EmailYaExiste() {
            RegisterRequest request = new RegisterRequest();
            request.setEmail("yhojan@streamgo.com");

            when(usuarioRepository.existsByEmail(request.getEmail())).thenReturn(true);

            assertThrows(RuntimeException.class, () -> authService.register(request));
            verify(usuarioRepository, never()).save(any(Usuario.class));
        }
    }

    @Nested
    @DisplayName("Escenarios de Inicio de Sesión (Login)")
    class LoginTests {

        @Test
        @DisplayName("Debería iniciar sesión correctamente con credenciales válidas")
        void login_Exito() {
            LoginRequest request = new LoginRequest();
            request.setEmail("yhojan@streamgo.com");
            request.setPassword("123456");

            when(usuarioRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(usuarioMock));
            when(passwordEncoder.matches(request.getPassword(), usuarioMock.getPassword())).thenReturn(true);
            when(jwtService.generateToken(usuarioMock)).thenReturn("mock-jwt-token");

            AuthResponse response = authService.login(request);

            assertNotNull(response);
            assertEquals("mock-jwt-token", response.getToken());
            assertNotNull(usuarioMock.getUltimoAcceso());
        }

        @Test
        @DisplayName("Debería fallar si el usuario no es encontrado")
        void login_UsuarioNoEncontrado() {
            LoginRequest request = new LoginRequest();
            request.setEmail("desconocido@streamgo.com");

            when(usuarioRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> authService.login(request));
        }

        @Test
        @DisplayName("Debería fallar si la contraseña es incorrecta")
        void login_ContrasenaIncorrecta() {
            LoginRequest request = new LoginRequest();
            request.setEmail("yhojan@streamgo.com");
            request.setPassword("erronea");

            when(usuarioRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(usuarioMock));
            when(passwordEncoder.matches(request.getPassword(), usuarioMock.getPassword())).thenReturn(false);

            assertThrows(RuntimeException.class, () -> authService.login(request));
        }
    }
}