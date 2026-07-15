package com.StreamGo.controller;

import com.StreamGo.dto.request.ActualizarContenidoRequest;
import com.StreamGo.dto.request.CrearContenidoRequest;
import com.StreamGo.dto.response.ContenidoResponse;
import com.StreamGo.service.ContenidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controlador administrativo para la gestión de contenidos.
 *
 * Permite al administrador crear, listar, actualizar,
 * desactivar y eliminar contenidos dentro de la plataforma StreamGo.
 * Incluye endpoints para subir archivos (imagen, banner, video).
 */
@RestController
@RequestMapping("/admin/contenidos")
@RequiredArgsConstructor
public class ContenidoAdminController {

    private final ContenidoService contenidoService;

    /**
     * Registra un nuevo contenido en la plataforma.
     * Versión sin archivos (solo URLs).
     *
     * @param request datos necesarios para crear el contenido.
     * @return contenido creado con su información principal.
     */
    @PostMapping
    public ResponseEntity<ContenidoResponse> crearContenido(
            @RequestBody CrearContenidoRequest request
    ) {
        return ResponseEntity.ok(
                contenidoService.crearContenido(request)
        );
    }

    /**
     * Registra un nuevo contenido en la plataforma con archivos.
     * Endpoint para subir imagen, banner y video junto con los datos del contenido.
     *
     * @param request datos del contenido en formato JSON.
     * @param imagen archivo de imagen (poster) - opcional.
     * @param banner archivo de banner - opcional.
     * @param video archivo de video - opcional.
     * @return contenido creado con las URLs de los archivos.
     */
    @PostMapping(value = "/crear-con-archivos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ContenidoResponse> crearContenidoConArchivos(
            @RequestPart("data") CrearContenidoRequest request,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen,
            @RequestPart(value = "banner", required = false) MultipartFile banner,
            @RequestPart(value = "video", required = false) MultipartFile video
    ) {
        return ResponseEntity.ok(
                contenidoService.crearContenidoConArchivos(request, imagen, banner, video)
        );
    }

    /**
     * Lista todos los contenidos registrados para administración.
     *
     * @return lista completa de contenidos.
     */
    @GetMapping
    public ResponseEntity<List<ContenidoResponse>> listarAdmin() {
        return ResponseEntity.ok(
                contenidoService.listarAdmin()
        );
    }

    /**
     * Obtiene un contenido por su ID para administración.
     *
     * @param id identificador del contenido.
     * @return contenido encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ContenidoResponse> obtenerContenido(
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.ok(
                contenidoService.obtenerContenidoPorId(id)
        );
    }

    /**
     * Actualiza la información de un contenido existente.
     * Versión sin archivos (solo URLs).
     *
     * @param id identificador del contenido.
     * @param request nuevos datos del contenido.
     * @return contenido actualizado.
     */
    @PutMapping("/{id}/editar")
    public ResponseEntity<ContenidoResponse> actualizarContenido(
            @PathVariable("id") Long id,
            @RequestBody ActualizarContenidoRequest request
    ) {
        return ResponseEntity.ok(
                contenidoService.actualizarContenido(id, request)
        );
    }

    /**
     * Actualiza un contenido existente con archivos.
     * Permite actualizar imagen, banner y video.
     * Los archivos son opcionales, solo se actualizan los que se envíen.
     *
     * @param id identificador del contenido.
     * @param request datos del contenido en formato JSON.
     * @param imagen archivo de imagen (poster) - opcional.
     * @param banner archivo de banner - opcional.
     * @param video archivo de video - opcional.
     * @return contenido actualizado con las URLs de los archivos.
     */
    @PutMapping(value = "/{id}/editar-con-archivos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ContenidoResponse> actualizarContenidoConArchivos(
            @PathVariable("id") Long id,
            @RequestPart("data") ActualizarContenidoRequest request,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen,
            @RequestPart(value = "banner", required = false) MultipartFile banner,
            @RequestPart(value = "video", required = false) MultipartFile video
    ) {
        return ResponseEntity.ok(
                contenidoService.actualizarContenidoConArchivos(id, request, imagen, banner, video)
        );
    }

    /**
     * Cambia el estado del contenido a INACTIVO.
     * No elimina el contenido, solo lo oculta.
     *
     * @param id identificador del contenido.
     * @return mensaje de confirmación.
     */
    @DeleteMapping("/{id}/desactivar")
    public ResponseEntity<String> desactivarContenido(
            @PathVariable("id") Long id
    ) {
        contenidoService.desactivarContenido(id);
        return ResponseEntity.ok("Contenido desactivado correctamente");
    }

    /**
     * Elimina un contenido permanentemente.
     * También elimina los archivos asociados (imagen, banner, video)
     * del servidor de archivos remoto.
     *
     * @param id identificador del contenido.
     * @return mensaje de confirmación.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarContenido(
            @PathVariable("id") Long id
    ) {
        contenidoService.eliminarContenido(id);
        return ResponseEntity.ok("Contenido eliminado correctamente");
    }
}