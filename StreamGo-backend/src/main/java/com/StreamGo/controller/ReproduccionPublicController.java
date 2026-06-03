package com.StreamGo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.StreamGo.dto.response.ReproduccionResponse;
import com.StreamGo.service.ReproduccionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/public/reproduccion")
@RequiredArgsConstructor
public class ReproduccionPublicController {

    private final ReproduccionService reproduccionService;

    @PostMapping("/{contenidoId}")
    public ResponseEntity<ReproduccionResponse> reproducirPublico(
            @PathVariable("contenidoId") Long contenidoId
    ) {
        return ResponseEntity.ok(
                reproduccionService.reproducirPublico(contenidoId)
        );
    }
}