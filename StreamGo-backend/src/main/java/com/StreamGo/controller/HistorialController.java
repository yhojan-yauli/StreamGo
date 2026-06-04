package com.StreamGo.controller;

import com.StreamGo.dto.response.HistorialResponse;
import com.StreamGo.service.HistorialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/historial")
@RequiredArgsConstructor
public class HistorialController {

    private final HistorialService historialService;

    @GetMapping
    public ResponseEntity<List<HistorialResponse>> obtenerHistorial(
            Authentication authentication
    ) {
        String email = authentication.getName();

        return ResponseEntity.ok(
                historialService.obtenerHistorial(email)
        );
    }
}