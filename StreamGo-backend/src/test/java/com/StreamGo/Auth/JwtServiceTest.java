package com.StreamGo.Auth;

import com.StreamGo.entity.Enum.Rol;
import com.StreamGo.entity.Usuario;
import com.StreamGo.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - JwtService")
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        // Inyectamos las propiedades @Value manualmente para el entorno de test
        ReflectionTestUtils.setField(jwtService, "secret", "superSecretKey1234567890123456789012345678901234567890");
        ReflectionTestUtils.setField(jwtService, "expiration", 3600000L); // 1 hora

        usuarioMock = Usuario.builder()
                .email("yhojan@streamgo.com")
                .rol(Rol.CLIENTE)
                .build();
    }

    @Test
    @DisplayName("Debería generar, extraer datos y validar un token JWT correctamente")
    void cicloDeVidaToken_Exito() {
        // 1. Generate
        String token = jwtService.generateToken(usuarioMock);
        assertNotNull(token);

        // 2. Extract
        String emailExtraido = jwtService.extractUsername(token);
        String rolExtraido = jwtService.extractRole(token);

        assertEquals("yhojan@streamgo.com", emailExtraido);
        assertEquals("CLIENTE", rolExtraido);

        // 3. Validate
        assertTrue(jwtService.isTokenValid(token, "yhojan@streamgo.com"));
        assertFalse(jwtService.isTokenValid(token, "incorrecto@streamgo.com"));
    }
}