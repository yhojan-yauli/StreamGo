package com.StreamGo.controller;

import com.StreamGo.dto.response.ContenidoResponse;
import com.StreamGo.service.ContenidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contenidos")
@RequiredArgsConstructor
public class ContenidoClienteController {

    private final ContenidoService contenidoService;

    // Usuario logueado sin suscripción: ve SINLOGIN e INACTIVO
    @GetMapping
    public ResponseEntity<List<ContenidoResponse>> listarClienteSinSuscripcion() {
        return ResponseEntity.ok(
                contenidoService.listarParaClienteSinSuscripcion()
        );
    }

    // Usuario logueado con suscripción: ve todo
    @GetMapping("/suscriptor")
    public ResponseEntity<List<ContenidoResponse>> listarClienteConSuscripcion() {
        return ResponseEntity.ok(
                contenidoService.listarParaClienteConSuscripcion()
        );
    }

@GetMapping("/categoria/{categoria}")
public ResponseEntity<List<ContenidoResponse>> listarPorCategoria(
        @PathVariable("categoria") String categoria
) {
        return ResponseEntity.ok(
                contenidoService.listarPorCategoria(categoria)
        );
    }

    @GetMapping("/recomendados")
    public ResponseEntity<List<ContenidoResponse>> listarRecomendados() {
        return ResponseEntity.ok(
                contenidoService.listarRecomendados()
        );
    }

    @GetMapping("/tendencias")
    public ResponseEntity<List<ContenidoResponse>> listarTendencias() {
        return ResponseEntity.ok(
                contenidoService.listarTendencias()
        );
    }

    //busca para content STRING titulo
@GetMapping("/buscar")
public ResponseEntity<List<ContenidoResponse>> buscarPorTitulo(
        @RequestParam("titulo") String titulo
) {
    return ResponseEntity.ok(
            contenidoService.buscarPorTitulo(titulo)
    );
}
}