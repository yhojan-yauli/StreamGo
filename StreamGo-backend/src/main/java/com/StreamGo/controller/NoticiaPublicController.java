package com.StreamGo.controller;

import com.StreamGo.dto.response.NoticiaResponse;
import com.StreamGo.service.NoticiaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST público que expone los endpoints de solo lectura
 * para las noticias en la aplicación StreamGo.
 */
@Slf4j
@RestController
@RequestMapping("/noticias")
@RequiredArgsConstructor
public class NoticiaPublicController {

    private final NoticiaService noticiaService;

    /**
     * Recibe una petición HTTP GET para listar todas las noticias sin filtro.
     *
     * @return {@link ResponseEntity} con la lista de {@link NoticiaResponse} y un estado HTTP 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<NoticiaResponse>> listarNoticias() {
        log.info("Petición REST pública recibida para LISTAR todas las noticias");
        return ResponseEntity.ok(
                noticiaService.listarNoticias()
        );
    }

    /**
     * Recibe una petición HTTP GET para obtener una noticia en específico por su identificador.
     *
     * @param idPost Identificador único de la noticia en la URL.
     * @return {@link ResponseEntity} con la {@link NoticiaResponse} encontrada y un estado HTTP 200 (OK).
     */
    @GetMapping("/{idPost}")
    public ResponseEntity<NoticiaResponse> obtenerNoticia(
            @PathVariable Long idPost
    ) {
        log.info("Petición REST pública recibida para OBTENER la noticia con ID: {}", idPost);
        return ResponseEntity.ok(
                noticiaService.obtenerNoticia(idPost)
        );
    }

    /**
     * Recibe una petición HTTP GET para obtener las noticias ordenadas,
     * priorizando aquellas que están fijadas.
     *
     * @return {@link ResponseEntity} con la lista de {@link NoticiaResponse} ordenadas y estado HTTP 200 (OK).
     */
    @GetMapping("/ordenadas")
    public ResponseEntity<List<NoticiaResponse>> listarNoticiasOrdenadas() {
        log.info("Petición REST pública recibida para LISTAR noticias ordenadas");
        return ResponseEntity.ok(
                noticiaService.listarNoticiasOrdenadas()
        );
    }
}