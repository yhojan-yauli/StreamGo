package com.StreamGo.controller;

import com.StreamGo.dto.response.ContenidoResponse;
import com.StreamGo.service.ContenidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para la visualización de contenidos por parte del cliente.
 *
 * Permite consultar el catálogo, buscar contenidos,
 * filtrar por categoría y visualizar recomendados o tendencias.
 */
@RestController
@RequestMapping("/contenidos")
@RequiredArgsConstructor
public class ContenidoClienteController {

    private final ContenidoService contenidoService;

    // Usuario logueado sin suscripción: ve SINLOGIN e INACTIVO
    /**
 * Controlador para la visualización de contenidos por parte del cliente.
 *
 * Permite consultar el catálogo, buscar contenidos,
 * filtrar por categoría y visualizar recomendados o tendencias.
 */
    @GetMapping
    public ResponseEntity<List<ContenidoResponse>> listarClienteSinSuscripcion() {
        return ResponseEntity.ok(
                contenidoService.listarParaClienteSinSuscripcion()
        );
    }

    // Usuario logueado con suscripción: ve todo
    /**
 * Lista el contenido disponible para clientes sin suscripción.
 *
 * @return lista de contenidos permitidos para usuarios logueados sin plan activo.
 */
    @GetMapping("/suscriptor")
    public ResponseEntity<List<ContenidoResponse>> listarClienteConSuscripcion() {
        return ResponseEntity.ok(
                contenidoService.listarParaClienteConSuscripcion()
        );
    }

/**
 * Lista el contenido por categoría.
 *
 * @param categoria nombre de la categoría.
 * @return lista de contenidos de la categoría especificada.
 */
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ContenidoResponse>> listarPorCategoria(
            @PathVariable("categoria") String categoria
    ) {
        return ResponseEntity.ok(
                contenidoService.listarPorCategoria(categoria)
        );
    }

/**
 * Lista los contenidos marcados como recomendados.
 *
 * @return lista de contenidos recomendados.
 */
    @GetMapping("/recomendados")
    public ResponseEntity<List<ContenidoResponse>> listarRecomendados() {
        return ResponseEntity.ok(
                contenidoService.listarRecomendados()
        );
    }

/**
 * Lista los contenidos marcados como tendencia.
 *
 * @return lista de contenidos en tendencia.
 */
    @GetMapping("/tendencias")
    public ResponseEntity<List<ContenidoResponse>> listarTendencias() {
        return ResponseEntity.ok(
                contenidoService.listarTendencias()
        );
    }

/**
 * Busca contenidos por título.
 *
 * @param titulo término de búsqueda.
 * @return lista de contenidos que coinciden con el título.
 */
    @GetMapping("/buscar")
    public ResponseEntity<List<ContenidoResponse>> buscarPorTitulo(
            @RequestParam("titulo") String titulo
    ) {
        return ResponseEntity.ok(
            contenidoService.buscarPorTitulo(titulo)
    );
}
}