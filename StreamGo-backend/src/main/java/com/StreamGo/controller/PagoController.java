package com.StreamGo.controller;


import com.StreamGo.dto.request.CrearPagoRequest;
import com.StreamGo.dto.response.PagoResponse;
import com.StreamGo.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PagoController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<PagoResponse> crearPago(
            @RequestBody CrearPagoRequest request,
            Authentication authentication
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                paymentService.crearPago(
                        request,
                        email
                )
        );
    }
}