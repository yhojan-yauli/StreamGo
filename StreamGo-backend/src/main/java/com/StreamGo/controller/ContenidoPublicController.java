package com.StreamGo.controller;

import com.StreamGo.dto.response.ContenidoResponse;
import com.StreamGo.service.ContenidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador público para consultar contenidos sin iniciar sesión.
 *
 * Solo expone contenidos configurados con estado SINLOGIN.
 */
@RestController
@RequestMapping("/public/contenidos")
@RequiredArgsConstructor
public class ContenidoPublicController {

    private final ContenidoService contenidoService;

    /**
     * Lista contenidos públicos disponibles para visitantes.
     *
     * @return lista de contenidos con estado SINLOGIN.
     */
    @GetMapping
    public ResponseEntity<List<ContenidoResponse>> listarSinLogin() {
        return ResponseEntity.ok(
                contenidoService.listarSinLogin()
        );
    }
}
