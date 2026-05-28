package com.StreamGo.controller;

import com.StreamGo.dto.response.ReproduccionResponse;
import com.StreamGo.service.ReproduccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reproduccion")
@RequiredArgsConstructor
public class ReproduccionController {

    private final ReproduccionService reproduccionService;

    @PostMapping("/{contenidoId}")
    public ResponseEntity<ReproduccionResponse> reproducir(
            @PathVariable Long contenidoId,
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