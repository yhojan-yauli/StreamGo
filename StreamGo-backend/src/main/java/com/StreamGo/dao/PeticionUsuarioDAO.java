package com.StreamGo.dao;

import com.StreamGo.entity.Peticion;
import com.StreamGo.entity.PeticionUsuario;
import com.StreamGo.entity.Usuario;

import java.util.Optional;

public interface PeticionUsuarioDAO
        extends IGenericDAO<PeticionUsuario, Long> {

    boolean existsByUsuarioAndPeticion(
            Usuario usuario,
            Peticion peticion
    );

    Optional<PeticionUsuario> findByUsuarioAndPeticion(
            Usuario usuario,
            Peticion peticion
    );
}
