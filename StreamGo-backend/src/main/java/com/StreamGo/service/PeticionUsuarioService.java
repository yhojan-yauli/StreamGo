package com.StreamGo.service;

import com.StreamGo.dao.PeticionDAO;
import com.StreamGo.dao.PeticionUsuarioDAO;
import com.StreamGo.dao.UsuarioDAO;
import com.StreamGo.entity.Peticion;
import com.StreamGo.entity.PeticionUsuario;
import com.StreamGo.entity.Usuario;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PeticionUsuarioService {

    private final PeticionUsuarioDAO peticionUsuarioDAO;
    private final PeticionDAO peticionDAO;
    private final UsuarioDAO usuarioDAO;

    // SELECCIONAR PELICULA
    public void seleccionar(Long peticionId) {

        // obtener email del usuario autenticado
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // buscar usuario
        Usuario usuario = usuarioDAO
                .findByEmail(email)
                .orElseThrow();

        // buscar pelicula
        Peticion peticion = peticionDAO.findById(peticionId);

        // verificar si ya existe
        boolean existe = peticionUsuarioDAO
                .existsByUsuarioAndPeticion(usuario, peticion);

        if (existe) {
            return;
        }

        // guardar seleccion
        PeticionUsuario peticionUsuario =
                PeticionUsuario.builder()
                        .usuario(usuario)
                        .peticion(peticion)
                        .build();

        peticionUsuarioDAO.save(peticionUsuario);
    }

    // QUITAR SELECCION
    public void quitar(Long peticionId) {

        // obtener email
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // buscar usuario
        Usuario usuario = usuarioDAO
                .findByEmail(email)
                .orElseThrow();

        // buscar pelicula
        Peticion peticion = peticionDAO.findById(peticionId);

        // buscar relacion
        PeticionUsuario peticionUsuario =
                peticionUsuarioDAO.findByUsuarioAndPeticion(
                        usuario,
                        peticion
                ).orElseThrow();

        peticionUsuarioDAO.delete(peticionUsuario.getId());
    }
}
