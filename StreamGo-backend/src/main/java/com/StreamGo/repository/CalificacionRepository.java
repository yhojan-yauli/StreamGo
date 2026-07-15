package com.StreamGo.repository;

import com.StreamGo.entity.CalificacionContenido;
import com.StreamGo.entity.Contenido;
import com.StreamGo.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * Elimina todas las calificaciones asociadas a un contenido.
     *
     * @param contenidoId ID del contenido.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM CalificacionContenido c WHERE c.contenido.id = :contenidoId")
    void deleteByContenidoId(@Param("contenidoId") Long contenidoId);
}