package com.StreamGo.repository;

import com.StreamGo.entity.Enum.Rol;
import com.StreamGo.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar Usuario por email
    Optional<Usuario> findByEmail(String email);

    // Verificar si ya existe email
    boolean existsByEmail(String email);

    // Obtener solo clientes
    List<Usuario> findByRol(Rol rol);

    // Obtener clientes paginados
    Page<Usuario> findByRol(Rol rol, Pageable pageable);
}