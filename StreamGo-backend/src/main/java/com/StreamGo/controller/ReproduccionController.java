package com.StreamGo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
