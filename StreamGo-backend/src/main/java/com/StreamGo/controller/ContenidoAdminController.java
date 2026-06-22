package com.StreamGo.controller;

import com.StreamGo.dto.request.ActualizarContenidoRequest;
import com.StreamGo.dto.request.CrearContenidoRequest;
import com.StreamGo.dto.response.ContenidoResponse;
import com.StreamGo.service.ContenidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Controlador administrativo para la gestión de contenidos.
 *
 * Permite al administrador crear, listar, actualizar,
 * desactivar y eliminar contenidos dentro de la plataforma StreamGo.
 */
@RestController
@RequestMapping("/admin/contenidos")
@RequiredArgsConstructor
public class ContenidoAdminController {

    private final ContenidoService contenidoService;
    /**
     * Registra un nuevo contenido en la plataforma.
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
     * Actualiza la información de un contenido existente.
     *
     * @param id identificador del contenido.
     * @param request nuevos datos del contenido.
     * @return contenido actualizado.
     */
    @PutMapping("/{id}/editar parametros de un contenido")
    public ResponseEntity<ContenidoResponse> actualizarContenido(
            @PathVariable("id") Long id,
            @RequestBody ActualizarContenidoRequest request
    ) {
        return ResponseEntity.ok(
                contenidoService.actualizarContenido(id, request)
        );
    }
    /**
     * Cambia el estado del contenido a INACTIVO.
     *
     * @param id identificador del contenido.
     * @return mensaje de confirmación.
     */
    @DeleteMapping("/{id}/desactivar")
    public ResponseEntity<String> desactivarContenido(
            @PathVariable("id") Long id
    ) {
        contenidoService.desactivarContenido(id);
        return ResponseEntity.ok("Contenido desactivado");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarContenido(
            @PathVariable("id") Long id
    ) {
        contenidoService.eliminarContenido(id);
        return ResponseEntity.ok("Contenido eliminado correctamente");
    }
}