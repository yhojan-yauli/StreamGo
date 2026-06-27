package com.StreamGo.dao;

import com.StreamGo.entity.HistorialReproduccion;
import com.StreamGo.entity.Usuario;

import java.util.List;

public interface HistorialReproduccionDAO
        extends IGenericDAO<HistorialReproduccion, Long> {

    List<HistorialReproduccion> findByUsuarioOrderByFechaReproduccionDesc(
            Usuario usuario
    );
}
