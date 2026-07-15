package com.StreamGo.controller;

import com.StreamGo.dto.request.PeticionRequest;
import com.StreamGo.dto.response.ContenidoVotableResponse;
import com.StreamGo.dto.response.PeticionResponse;
import com.StreamGo.service.PeticionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/peticiones")
@RequiredArgsConstructor
public class PeticionClienteController {

    private final PeticionService peticionService;

    @GetMapping("/lista")
    public ResponseEntity<List<ContenidoVotableResponse>> listar() {
        return ResponseEntity.ok(peticionService.listarVotables());
    }

    @PostMapping("/elegir")
    public ResponseEntity<PeticionResponse> elegir(
            @Valid @RequestBody PeticionRequest request,
            @AuthenticationPrincipal String email
    ) {
        return ResponseEntity.ok(
                peticionService.elegirPelicula(email, request)
        );
    }
}
