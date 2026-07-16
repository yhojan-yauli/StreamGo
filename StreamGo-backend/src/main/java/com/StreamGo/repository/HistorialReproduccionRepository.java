package com.StreamGo.repository;

import com.StreamGo.entity.HistorialReproduccion;
import com.StreamGo.entity.Usuario;
import com.StreamGo.entity.Contenido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface HistorialReproduccionRepository
        extends JpaRepository<HistorialReproduccion, Long> {

    List<HistorialReproduccion> findByUsuarioOrderByFechaReproduccionDesc(
            Usuario usuario
    );

    @Query("SELECT h FROM HistorialReproduccion h WHERE h.usuario = :usuario AND h.contenido = :contenido ORDER BY h.fechaReproduccion DESC")
    List<HistorialReproduccion> findLastByUsuarioAndContenido(
            @Param("usuario") Usuario usuario,
            @Param("contenido") Contenido contenido
    );

    @Modifying
    @Transactional
    @Query("DELETE FROM HistorialReproduccion h WHERE h.contenido.id = :contenidoId")
    void deleteByContenidoId(@Param("contenidoId") Long contenidoId);
}