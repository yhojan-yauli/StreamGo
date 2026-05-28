
package com.StreamGo.controller;

import com.StreamGo.entity.Peticion;
import com.StreamGo.service.PeticionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/peticiones")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminPeticionController {

    private final PeticionService service;

    // AGREGAR PELICULA
    @PostMapping
    public Peticion crear(@RequestBody Peticion peticion) {
        return service.guardar(peticion);
    }

    // EDITAR PELICULA
    @PutMapping("/{id}")
    public Peticion actualizar(
            @PathVariable Long id,
            @RequestBody Peticion peticion
    ) {
        peticion.setId(id);
        return service.guardar(peticion);
    }

    // ELIMINAR PELICULA
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}