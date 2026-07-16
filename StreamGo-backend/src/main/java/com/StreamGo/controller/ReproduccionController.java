package com.StreamGo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.StreamGo.dto.response.ReproduccionResponse;
import com.StreamGo.service.ReproduccionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reproduccion")
@RequiredArgsConstructor
public class ReproduccionController {

    private final ReproduccionService reproduccionService;

    @PostMapping("/{contenidoId}")
    public ResponseEntity<ReproduccionResponse> reproducir(
            @PathVariable("contenidoId") Long contenidoId,
            Authentication authentication
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(
                reproduccionService.reproducir(
                        contenidoId,
                        email
                )
        );
    }

    @PatchMapping("/{contenidoId}/progreso")
    public ResponseEntity<Void> actualizarProgreso(
            @PathVariable("contenidoId") Long contenidoId,
            @RequestParam("progreso") Integer progresoSegundos,
            @RequestParam(value = "completado", required = false) Boolean completado,
            Authentication authentication
    ) {
        String email = authentication.getName();
        reproduccionService.actualizarProgreso(contenidoId, email, progresoSegundos, completado);
        return ResponseEntity.ok().build();
    }
}