package com.StreamGo.repository;

import com.StreamGo.entity.HistorialReproduccion;
import com.StreamGo.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialReproduccionRepository
        extends JpaRepository<HistorialReproduccion, Long> {

    List<HistorialReproduccion> findByUsuarioOrderByFechaReproduccionDesc(
            Usuario usuario
    );
}