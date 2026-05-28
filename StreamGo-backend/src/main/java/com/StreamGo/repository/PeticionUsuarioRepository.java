package com.StreamGo.repository;

import com.StreamGo.entity.Peticion;
import com.StreamGo.entity.PeticionUsuario;
import com.StreamGo.entity.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PeticionUsuarioRepository
        extends JpaRepository<PeticionUsuario, Long> {

    boolean existsByUsuarioAndPeticion(
            Usuario usuario,
            Peticion peticion
    );

    Optional<PeticionUsuario> findByUsuarioAndPeticion(
            Usuario usuario,
            Peticion peticion
    );
}