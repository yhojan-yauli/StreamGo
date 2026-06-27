package com.StreamGo.dao;

import com.StreamGo.entity.CalificacionContenido;
import com.StreamGo.entity.Contenido;
import com.StreamGo.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface CalificacionDAO
        extends IGenericDAO<CalificacionContenido, Long> {

    Optional<CalificacionContenido> findByUsuarioAndContenido(
            Usuario usuario,
            Contenido contenido
    );

    List<CalificacionContenido> findByContenido(Contenido contenido);
}
