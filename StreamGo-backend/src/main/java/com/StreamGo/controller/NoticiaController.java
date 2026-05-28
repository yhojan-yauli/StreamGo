package com.StreamGo.controller;

import com.StreamGo.dto.request.NoticiaRequest;
import com.StreamGo.dto.response.NoticiaResponse;
import com.StreamGo.service.NoticiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/noticias")
@RequiredArgsConstructor
public class NoticiaController {

    private final NoticiaService noticiaService;

    @PostMapping
    public ResponseEntity<NoticiaResponse> crearNoticia(
            @RequestBody NoticiaRequest request
    ) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(noticiaService.crearNoticia(request));
    }

    @GetMapping
    public ResponseEntity<List<NoticiaResponse>> listarNoticias() {

        return ResponseEntity.ok(
                noticiaService.listarNoticias()
        );
    }

    @GetMapping("/{idPost}")
    public ResponseEntity<NoticiaResponse> obtenerNoticia(
            @PathVariable Long idPost
    ) {

        return ResponseEntity.ok(
                noticiaService.obtenerNoticia(idPost)
        );
    }

    @GetMapping("/autor/{idAutor}")
    public ResponseEntity<List<NoticiaResponse>> listarPorAutor(
            @PathVariable Long idAutor
    ) {

        return ResponseEntity.ok(
                noticiaService.listarPorAutor(idAutor)
        );
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<NoticiaResponse>> listarPorUsuario(
            @PathVariable Long idUsuario
    ) {

        return ResponseEntity.ok(
                noticiaService.listarPorUsuario(idUsuario)
        );
    }

    @PutMapping("/{idPost}")
    public ResponseEntity<NoticiaResponse> actualizarNoticia(
            @PathVariable Long idPost,
            @RequestBody NoticiaRequest request
    ) {

        return ResponseEntity.ok(
                noticiaService.actualizarNoticia(idPost, request)
        );
    }

    @PatchMapping("/{idPost}/reaccionar")
    public ResponseEntity<NoticiaResponse> reaccionar(
            @PathVariable Long idPost
    ) {

        return ResponseEntity.ok(
                noticiaService.reaccionar(idPost)
        );
    }

    @DeleteMapping("/{idPost}")
    public ResponseEntity<String> eliminarNoticia(
            @PathVariable Long idPost
    ) {

        noticiaService.eliminarNoticia(idPost);

        return ResponseEntity.ok("Noticia eliminada");
    }
}
