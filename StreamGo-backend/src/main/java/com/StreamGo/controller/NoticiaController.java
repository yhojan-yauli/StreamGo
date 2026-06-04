package com.StreamGo.controller;

import com.StreamGo.dto.request.NoticiaRequest;
import com.StreamGo.dto.response.NoticiaResponse;
import com.StreamGo.service.NoticiaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/noticias")
@RequiredArgsConstructor
public class NoticiaController {

    private final NoticiaService noticiaService;

    @PostMapping
    public ResponseEntity<NoticiaResponse> crearNoticia(
            @RequestBody NoticiaRequest request
    ) {
        log.info("Petición REST recibida para CREAR noticia");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(noticiaService.crearNoticia(request));
    }

    @GetMapping
    public ResponseEntity<List<NoticiaResponse>> listarNoticias() {
        log.info("Petición REST recibida para LISTAR todas las noticias");
        return ResponseEntity.ok(
                noticiaService.listarNoticias()
        );
    }

    @GetMapping("/{idPost}")
    public ResponseEntity<NoticiaResponse> obtenerNoticia(
            @PathVariable Long idPost
    ) {
        log.info("Petición REST recibida para OBTENER la noticia con ID: {}", idPost);
        return ResponseEntity.ok(
                noticiaService.obtenerNoticia(idPost)
        );
    }

    @GetMapping("/autor/{idAutor}")
    public ResponseEntity<List<NoticiaResponse>> listarPorAutor(
            @PathVariable Long idAutor
    ) {
        log.info("Petición REST recibida para LISTAR noticias del autor ID: {}", idAutor);
        return ResponseEntity.ok(
                noticiaService.listarPorAutor(idAutor)
        );
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<NoticiaResponse>> listarPorUsuario(
            @PathVariable Long idUsuario
    ) {
        log.info("Petición REST recibida para LISTAR noticias del usuario ID: {}", idUsuario);
        return ResponseEntity.ok(
                noticiaService.listarPorUsuario(idUsuario)
        );
    }

    @PutMapping("/{idPost}")
    public ResponseEntity<NoticiaResponse> actualizarNoticia(
            @PathVariable Long idPost,
            @RequestBody NoticiaRequest request
    ) {
        log.info("Petición REST recibida para ACTUALIZAR la noticia con ID: {}", idPost);
        return ResponseEntity.ok(
                noticiaService.actualizarNoticia(idPost, request)
        );
    }

    @PatchMapping("/{idPost}/reaccionar")
    public ResponseEntity<NoticiaResponse> reaccionar(
            @PathVariable Long idPost
    ) {
        log.info("Petición REST recibida para REACCIONAR a la noticia con ID: {}", idPost);
        return ResponseEntity.ok(
                noticiaService.reaccionar(idPost)
        );
    }

    @DeleteMapping("/{idPost}")
    public ResponseEntity<String> eliminarNoticia(
            @PathVariable Long idPost
    ) {
        log.info("Petición REST recibida para ELIMINAR la noticia con ID: {}", idPost);
        noticiaService.eliminarNoticia(idPost);
        return ResponseEntity.ok("Noticia eliminada");
    }
}