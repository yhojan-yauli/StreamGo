package com.StreamGo.controller;

import com.StreamGo.dto.response.ContenidoResponse;
import com.StreamGo.service.ContenidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para la visualización de contenidos por parte del cliente.
 *
 * Permite consultar el catálogo según el estado real del usuario,
 * buscar contenidos, filtrar por categoría y visualizar recomendados o tendencias.
 */
@RestController
@RequestMapping("/contenidos")
@RequiredArgsConstructor
public class ContenidoClienteController {

    private final ContenidoService contenidoService;

    /**
     * Lista el contenido permitido para el usuario autenticado.
     *
     * Reglas:
     * - Usuario ACTIVO o con suscripción activa: ve todo.
     * - Usuario INACTIVO: ve INACTIVO y SINLOGIN.
     * - Usuario SUSPENDIDO: no puede consultar contenidos.
     *
     * @param authentication información del usuario autenticado.
     * @return lista de contenidos permitidos para el usuario.
     */
    @GetMapping
    public ResponseEntity<List<ContenidoResponse>> listarContenidos(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(
                contenidoService.listarParaUsuario(email)
        );
    }

    /**
     * Lista el catálogo completo para usuarios con suscripción.
     *
     * @return lista completa de contenidos.
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
