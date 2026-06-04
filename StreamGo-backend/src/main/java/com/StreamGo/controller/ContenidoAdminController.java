package com.StreamGo.controller;

import com.StreamGo.dto.request.ActualizarContenidoRequest;
import com.StreamGo.dto.request.CrearContenidoRequest;
import com.StreamGo.dto.response.ContenidoResponse;
import com.StreamGo.service.ContenidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/contenidos")
@RequiredArgsConstructor
public class ContenidoAdminController {

    private final ContenidoService contenidoService;

    @PostMapping
    public ResponseEntity<ContenidoResponse> crearContenido(
            @RequestBody CrearContenidoRequest request
    ) {
        return ResponseEntity.ok(
                contenidoService.crearContenido(request)
        );
    }

    @GetMapping
    public ResponseEntity<List<ContenidoResponse>> listarAdmin() {
        return ResponseEntity.ok(
                contenidoService.listarAdmin()
        );
    }

    @PutMapping("/{id}/editar parametros de un contenido")
    public ResponseEntity<ContenidoResponse> actualizarContenido(
            @PathVariable("id") Long id,
            @RequestBody ActualizarContenidoRequest request
    ) {
        return ResponseEntity.ok(
                contenidoService.actualizarContenido(id, request)
        );
    }

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