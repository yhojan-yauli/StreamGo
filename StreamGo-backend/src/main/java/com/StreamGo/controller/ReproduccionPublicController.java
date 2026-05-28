package com.StreamGo.controller;

import com.StreamGo.dto.response.ReproduccionResponse;
import com.StreamGo.service.ReproduccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/public/reproduccion")
@RequiredArgsConstructor
public class ReproduccionPublicController {

    private final ReproduccionService reproduccionService;

@PostMapping("/{contenidoId}")
public ResponseEntity<ReproduccionResponse> reproducirPublico(
        @PathVariable Long contenidoId
) {

    return ResponseEntity.ok(
            reproduccionService.reproducirPublico(contenidoId)
    );
}
}