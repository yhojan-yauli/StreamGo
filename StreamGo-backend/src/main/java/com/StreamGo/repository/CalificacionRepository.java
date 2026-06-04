package com.StreamGo.repository;

import com.StreamGo.entity.CalificacionContenido;
import com.StreamGo.entity.Contenido;
import com.StreamGo.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CalificacionRepository extends JpaRepository<CalificacionContenido, Long> {

    Optional<CalificacionContenido> findByUsuarioAndContenido(
            Usuario usuario,
            Contenido contenido
    );

    List<CalificacionContenido> findByContenido(
            Contenido contenido
    );
}