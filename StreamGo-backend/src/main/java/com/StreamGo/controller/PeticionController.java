package com.StreamGo.controller;

import com.StreamGo.entity.Peticion;
import com.StreamGo.service.PeticionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/peticiones")
@RequiredArgsConstructor
public class PeticionController {

    private final PeticionService service;

    // LISTAR TODAS LAS PELICULAS
    @GetMapping
    public List<Peticion> listar() {
        return service.listarTodas();
    }

    // OBTENER UNA PELICULA
    @GetMapping("/{id}")
    public Peticion obtener(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }
}
