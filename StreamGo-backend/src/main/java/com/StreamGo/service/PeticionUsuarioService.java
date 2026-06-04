package com.StreamGo.service;

import com.StreamGo.entity.Peticion;
import com.StreamGo.entity.PeticionUsuario;
import com.StreamGo.entity.Usuario;
import com.StreamGo.repository.PeticionRepository;
import com.StreamGo.repository.PeticionUsuarioRepository;
import com.StreamGo.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PeticionUsuarioService {

    private final PeticionUsuarioRepository repository;
    private final PeticionRepository peticionRepository;
    private final UsuarioRepository usuarioRepository;

    // SELECCIONAR PELICULA
    public void seleccionar(Long peticionId) {

        // obtener email del usuario autenticado
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // buscar usuario
        Usuario usuario = usuarioRepository
                .findByEmail(email)
                .orElseThrow();

        // buscar pelicula
        Peticion peticion = peticionRepository
                .findById(peticionId)
                .orElseThrow();

        // verificar si ya existe
        boolean existe = repository
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

        repository.save(peticionUsuario);
    }

    // QUITAR SELECCION
    public void quitar(Long peticionId) {

        // obtener email
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // buscar usuario
        Usuario usuario = usuarioRepository
                .findByEmail(email)
                .orElseThrow();

        // buscar pelicula
        Peticion peticion = peticionRepository
                .findById(peticionId)
                .orElseThrow();

        // buscar relacion
        PeticionUsuario peticionUsuario =
                repository.findByUsuarioAndPeticion(
                        usuario,
                        peticion
                ).orElseThrow();

        repository.delete(peticionUsuario);
    }
}