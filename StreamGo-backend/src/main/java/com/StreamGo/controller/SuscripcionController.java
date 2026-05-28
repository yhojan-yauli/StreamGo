package com.StreamGo.controller;

import com.StreamGo.entity.Suscripcion;
import com.StreamGo.service.SuscripcionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/suscripciones")
@RequiredArgsConstructor
public class SuscripcionController {

    private final SuscripcionService suscripcionService;

    /*
     * OBTENER SUSCRIPCIÓN POR USUARIO
     */

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Suscripcion> obtenerSuscripcion(
            @PathVariable Long usuarioId
    ) {

        return ResponseEntity.ok(
                suscripcionService.obtenerSuscripcionUsuario(usuarioId)
        );
    }

    /*
     * VERIFICAR SI LA SUSCRIPCIÓN EXPIRÓ
     */

    @PutMapping("/verificar/{usuarioId}")
    public ResponseEntity<Suscripcion> verificarEstado(
            @PathVariable Long usuarioId
    ) {

        return ResponseEntity.ok(
                suscripcionService.verificarExpiracion(usuarioId)
        );
    }

}