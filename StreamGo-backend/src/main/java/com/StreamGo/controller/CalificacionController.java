package com.StreamGo.controller;

import com.StreamGo.dto.request.CalificacionRequest;
import com.StreamGo.dto.response.CalificacionResponse;
import com.StreamGo.service.CalificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
/**
 * Controlador encargado de gestionar las calificaciones de contenido.
 *
 * Permite que los usuarios autenticados califiquen películas
 * o contenidos disponibles en la plataforma.
 */
@RestController
@RequestMapping("/calificaciones")
@RequiredArgsConstructor
public class CalificacionController {

    private final CalificacionService calificacionService;
/**
 * Registra o actualiza la calificación de un contenido.
 *
 * @param contenidoId identificador del contenido calificado.
 * @param request datos de la calificación.
 * @param authentication información del usuario autenticado.
 * @return respuesta con el promedio actualizado.
 */
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