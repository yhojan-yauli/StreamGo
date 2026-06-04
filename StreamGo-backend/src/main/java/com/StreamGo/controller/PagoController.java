package com.StreamGo.controller;


import com.StreamGo.dto.request.CrearPagoRequest;
import com.StreamGo.dto.response.PagoResponse;
import com.StreamGo.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de pagos del sistema StreamGo.
 * Maneja la creación de pagos para usuarios autenticados.
 *
 * @author Yhojan Yauli
 * @version 1.0
 */
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PagoController {

    private final PaymentService paymentService;

    /**
     * Crea un pago para el usuario autenticado.
     *
     * @param request datos del pago a realizar
     * @param authentication información del usuario autenticado
     * @return respuesta con el detalle del pago generado
     */
    @PostMapping("/create")
    public ResponseEntity<PagoResponse> crearPago(
            @RequestBody CrearPagoRequest request,
            Authentication authentication
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                paymentService.crearPago(request, email)
        );
    }
}