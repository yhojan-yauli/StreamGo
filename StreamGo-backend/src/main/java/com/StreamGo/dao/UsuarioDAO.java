package com.StreamGo.dao;

import com.StreamGo.entity.Enum.Rol;
import com.StreamGo.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioDAO extends IGenericDAO<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Usuario> findByRol(Rol rol);
}
