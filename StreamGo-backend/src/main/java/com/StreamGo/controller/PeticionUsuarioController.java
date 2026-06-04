package com.StreamGo.controller;

import com.StreamGo.service.PeticionUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/peticiones")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class PeticionUsuarioController {

    private final PeticionUsuarioService service;

    // MARCAR COMO DESEADA
    @PostMapping("/{id}/seleccionar")
    public void seleccionar(@PathVariable Long id) {
        service.seleccionar(id);
    }

    // QUITAR DE DESEADOS
    @DeleteMapping("/{id}/quitar")
    public void quitar(@PathVariable Long id) {
        service.quitar(id);
    }
}