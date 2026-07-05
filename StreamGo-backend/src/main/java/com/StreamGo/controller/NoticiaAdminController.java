package com.StreamGo.controller;

import com.StreamGo.dto.query.NoticiaQuery;
import com.StreamGo.dto.request.NoticiaRequest;
import com.StreamGo.dto.response.NoticiaResponse;
import com.StreamGo.dto.response.PageResponse;
import com.StreamGo.service.NoticiaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Controlador REST administrativo que expone los endpoints para la gestión
 * y moderación completa de las noticias en la aplicación StreamGo.
 */
@Slf4j
@RestController
@RequestMapping("/admin/noticias")
@RequiredArgsConstructor
public class NoticiaAdminController {

    private final NoticiaService noticiaService;

    /**
     * Recibe una petición HTTP GET para listar noticias administrativas
     * aplicando filtros, ordenamiento y paginación.
     *
     * @param search Texto de búsqueda.
     * @param estado Estado visual de noticia: todos, fijadas o normales.
     * @param sort Orden: recientes, reacciones o titulo.
     * @param page Número de página base 0.
     * @param size Cantidad de registros por página.
     * @return {@link ResponseEntity} con una página de {@link NoticiaResponse}.
     */
    @GetMapping
    public ResponseEntity<PageResponse<NoticiaResponse>> listarNoticiasAdmin(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        log.info("Petición REST administrativa recibida para LISTAR noticias paginadas");
        return ResponseEntity.ok(
                noticiaService.buscarNoticiasAdmin(
                        NoticiaQuery.of(search, estado, sort, page, size)
                )
        );
    }

    /**
     * Alias administrativo para buscar noticias sin romper clientes que prefieran
     * una ruta explícita de búsqueda.
     *
     * @param search Texto de búsqueda.
     * @param estado Estado visual de noticia: todos, fijadas o normales.
     * @param sort Orden: recientes, reacciones o titulo.
     * @param page Número de página base 0.
     * @param size Cantidad de registros por página.
     * @return {@link ResponseEntity} con una página de {@link NoticiaResponse}.
     */
    @GetMapping("/buscar")
    public ResponseEntity<PageResponse<NoticiaResponse>> buscarNoticiasAdmin(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        log.info("Petición REST administrativa recibida para BUSCAR noticias");
        return ResponseEntity.ok(
                noticiaService.buscarNoticiasAdmin(
                        NoticiaQuery.of(search, estado, sort, page, size)
                )
        );
    }

    /**
     * Recibe una petición HTTP POST para crear una nueva noticia.
     *
     * @param request Cuerpo de la petición que contiene los datos de la noticia.
     * @return {@link ResponseEntity} con el objeto {@link NoticiaResponse} creado y un estado HTTP 201 (CREATED).
     */
    @PostMapping
    public ResponseEntity<NoticiaResponse> crearNoticia(
            @RequestBody NoticiaRequest request,
            Authentication authentication
    ) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Administrador autenticado no identificado");
        }

        log.info("Petición REST administrativa recibida para CREAR noticia");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(noticiaService.crearNoticia(request, authentication.getName()));
    }

    /**
     * Recibe una petición HTTP PUT para actualizar completamente los datos de una noticia.
     *
     * @param idPost Identificador único de la noticia a actualizar en la URL.
     * @param request Cuerpo de la petición que contiene los nuevos datos de la noticia.
     * @return {@link ResponseEntity} con el objeto {@link NoticiaResponse} actualizado y un estado HTTP 200 (OK).
     */
    @PutMapping("/{idPost}")
    public ResponseEntity<NoticiaResponse> actualizarNoticia(
            @PathVariable Long idPost,
            @RequestBody NoticiaRequest request
    ) {
        log.info("Petición REST administrativa recibida para ACTUALIZAR la noticia con ID: {}", idPost);
        return ResponseEntity.ok(
                noticiaService.actualizarNoticia(idPost, request)
        );
    }

    /**
     * Recibe una petición HTTP DELETE para eliminar definitivamente una noticia.
     *
     * @param idPost Identificador único de la noticia a eliminar en la URL.
     * @return {@link ResponseEntity} con un mensaje de confirmación y estado HTTP 200 (OK).
     */
    @DeleteMapping("/{idPost}")
    public ResponseEntity<String> eliminarNoticia(
            @PathVariable Long idPost
    ) {
        log.info("Petición REST administrativa recibida para ELIMINAR la noticia con ID: {}", idPost);
        noticiaService.eliminarNoticia(idPost);
        return ResponseEntity.ok("Noticia eliminada");
    }

    /**
     * Recibe una petición HTTP PATCH para alternar el estado fijado de una noticia específica.
     *
     * @param idPost Identificador de la noticia a fijar/desfijar en la URL.
     * @return {@link ResponseEntity} con el objeto {@link NoticiaResponse} actualizado y estado HTTP 200 (OK).
     */
    @PatchMapping("/{idPost}/fijar")
    public ResponseEntity<NoticiaResponse> fijarNoticia(
            @PathVariable Long idPost
    ) {
        log.info("Petición REST administrativa recibida para MODIFICAR estado fijado de la noticia ID: {}", idPost);
        return ResponseEntity.ok(
                noticiaService.fijarNoticia(idPost)
        );
    }

    /**
     * Recibe una petición HTTP GET para obtener todas las noticias asociadas a un autor específico.
     *
     * @param idAutor Identificador del autor en la URL.
     * @return {@link ResponseEntity} con la lista de {@link NoticiaResponse} y un estado HTTP 200 (OK).
     */
    @GetMapping("/autor/{idAutor}")
    public ResponseEntity<List<NoticiaResponse>> listarPorAutor(
            @PathVariable Long idAutor
    ) {
        log.info("Petición REST administrativa recibida para LISTAR noticias del autor ID: {}", idAutor);
        return ResponseEntity.ok(
                noticiaService.listarPorAutor(idAutor)
        );
    }

    /**
     * Recibe una petición HTTP GET para obtener todas las noticias asociadas a un usuario específico.
     *
     * @param idUsuario Identificador del usuario en la URL.
     * @return {@link ResponseEntity} con la lista de {@link NoticiaResponse} y un estado HTTP 200 (OK).
     */
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<NoticiaResponse>> listarPorUsuario(
            @PathVariable Long idUsuario
    ) {
        log.info("Petición REST administrativa recibida para LISTAR noticias del usuario ID: {}", idUsuario);
        return ResponseEntity.ok(
                noticiaService.listarPorUsuario(idUsuario)
        );
    }
}
