package com.StreamGo.controller;

import com.StreamGo.entity.Suscripcion;
import com.StreamGo.service.SuscripcionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de suscripciones para clientes.
 * Permite consultar y validar la suscripción del usuario autenticado.
 *
 * @author Yhojan Yauli
 * @version 1.0
 */
@RestController
@RequestMapping("/cliente/suscripciones")
@RequiredArgsConstructor
public class SuscripcionClienteController {

    private final SuscripcionService suscripcionService;

    /**
     * Obtiene la suscripción del usuario autenticado.
     * Incluye cálculo de horas restantes en tiempo real.
     *
     * @return suscripción del usuario logueado
     */
    @GetMapping("/mi-suscripcion")
    public ResponseEntity<Suscripcion> miSuscripcion() {

        Suscripcion suscripcion =
                suscripcionService.obtenerSuscripcionUsuario();

        long horasRestantes =
                suscripcionService.calcularHorasRestantes(suscripcion);

        suscripcion.setHorasRestantes((int) horasRestantes);

        return ResponseEntity.ok(suscripcion);
    }

    /**
     * Verifica el estado de la suscripción del usuario autenticado.
     *
     * @return suscripción actualizada con estado verificado
     */
    @PutMapping("/verificar")
    public ResponseEntity<Suscripcion> verificar() {

        return ResponseEntity.ok(
                suscripcionService.verificarExpiracion()
        );
    }
}