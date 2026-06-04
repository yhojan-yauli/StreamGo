package com.StreamGo.controller;

import com.StreamGo.dto.response.HistorialResponse;
import com.StreamGo.service.HistorialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Controlador encargado de consultar el historial de reproducción.
 *
 * Permite al usuario autenticado visualizar los contenidos
 * que ha reproducido anteriormente.
 */
@RestController
@RequestMapping("/historial")
@RequiredArgsConstructor
public class HistorialController {

    private final HistorialService historialService;
/**
 * Obtiene el historial de reproducción del usuario autenticado.
 *
 * @param authentication información del usuario autenticado.
 * @return lista de contenidos reproducidos por el usuario.
 */
    @GetMapping
    public ResponseEntity<List<HistorialResponse>> obtenerHistorial(
            Authentication authentication
    ) {
        String email = authentication.getName();

        return ResponseEntity.ok(
                historialService.obtenerHistorial(email)
        );
    }
}