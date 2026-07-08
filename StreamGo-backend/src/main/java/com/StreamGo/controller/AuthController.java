package com.StreamGo.controller;

import com.StreamGo.dto.response.AuthResponse;
import com.StreamGo.dto.request.LoginRequest;
import com.StreamGo.dto.request.RegisterRequest;
import com.StreamGo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de autenticación del sistema StreamGo.
 * Expone endpoints para registro e inicio de sesión de usuarios.
 *
 * @author Yhojan Yauli
 * @version 1.0
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param request datos del usuario a registrar
     * @return respuesta con estado de creación de cuenta
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request
    ) {
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Autentica un usuario en el sistema.
     *
     * @param request credenciales de acceso
     * @return respuesta con token o estado de autenticación
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request
    ) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}