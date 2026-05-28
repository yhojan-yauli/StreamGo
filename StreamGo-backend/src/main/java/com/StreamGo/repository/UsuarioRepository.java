package com.StreamGo.repository;

import com.StreamGo.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar Usuario por email
    Optional<Usuario> findByEmail(String email);

    // Verificar si ya existe email
    boolean existsByEmail(String email);
}