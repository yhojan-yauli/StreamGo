package com.StreamGo.controller;

import com.StreamGo.dto.response.NoticiaResponse;
import com.StreamGo.service.NoticiaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para acciones de clientes/usuarios autenticados
 * sobre las noticias en la aplicación StreamGo.
 */
@Slf4j
@RestController
@RequestMapping("/cliente/noticias")
@RequiredArgsConstructor
public class NoticiaClienteController {

    private final NoticiaService noticiaService;

    /**
     * Recibe una petición HTTP PATCH para sumar una reacción a la noticia indicada.
     *
     * @param idPost Identificador único de la noticia en la URL.
     * @return {@link ResponseEntity} con el objeto {@link NoticiaResponse} actualizado y un estado HTTP 200 (OK).
     */
    @PatchMapping("/{idPost}/reaccionar")
    public ResponseEntity<NoticiaResponse> reaccionar(
            @PathVariable Long idPost
    ) {
        log.info("Petición REST de cliente recibida para REACCIONAR a la noticia con ID: {}", idPost);
        return ResponseEntity.ok(
                noticiaService.reaccionar(idPost)
        );
    }
}