package com.StreamGo.controller;

import com.StreamGo.dto.request.CalificacionRequest;
import com.StreamGo.dto.response.CalificacionResponse;
import com.StreamGo.service.CalificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/calificaciones")
@RequiredArgsConstructor
public class CalificacionController {

    private final CalificacionService calificacionService;

    @PostMapping("/{contenidoId}")
    public ResponseEntity<CalificacionResponse> calificarContenido(
            @PathVariable("contenidoId") Long contenidoId,
            @RequestBody CalificacionRequest request,
            Authentication authentication
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                calificacionService.calificarContenido(
                        contenidoId,
                        email,
                        request
                )
        );
    }
}