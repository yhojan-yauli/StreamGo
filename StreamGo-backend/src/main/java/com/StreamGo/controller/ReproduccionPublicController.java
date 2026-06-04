package com.StreamGo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.StreamGo.dto.response.ReproduccionResponse;
import com.StreamGo.service.ReproduccionService;

import lombok.RequiredArgsConstructor;
/**
 * Controlador público para mostrar contenidos disponibles sin iniciar sesión.
 *
 * Permite que visitantes accedan únicamente a contenidos
 * configurados con estado SINLOGIN.
 */
@RestController
@RequestMapping("/public/reproduccion")
@RequiredArgsConstructor
public class ReproduccionPublicController {

    private final ReproduccionService reproduccionService;
/**
 * Lista los contenidos públicos disponibles para visitantes.
 *
 * @return lista de contenidos con estado SINLOGIN.
 */
    @PostMapping("/{contenidoId}")
    public ResponseEntity<ReproduccionResponse> reproducirPublico(
            @PathVariable("contenidoId") Long contenidoId
    ) {
        return ResponseEntity.ok(
                reproduccionService.reproducirPublico(contenidoId)
        );
    }
}