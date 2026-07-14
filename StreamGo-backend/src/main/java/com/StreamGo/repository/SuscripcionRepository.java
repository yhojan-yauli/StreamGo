package com.StreamGo.repository;

import com.StreamGo.entity.Enum.EstadoSuscripcion;
import com.StreamGo.entity.Suscripcion;
import com.StreamGo.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SuscripcionRepository
        extends JpaRepository<Suscripcion, Long> {

    @EntityGraph(attributePaths = {"usuario", "plan"})
    List<Suscripcion> findByUsuarioId(Long usuarioId);

    @EntityGraph(attributePaths = {"usuario", "plan"})
    List<Suscripcion> findByUsuario(Usuario usuario);

    @EntityGraph(attributePaths = {"usuario", "plan"})
    List<Suscripcion> findByUsuarioIdAndEstado(
            Long usuarioId,
            EstadoSuscripcion estado
    );

    List<Suscripcion> findByUsuarioAndEstado(
            Usuario usuario,
            EstadoSuscripcion estado
    );

    Optional<Suscripcion> findTopByUsuarioIdOrderByFechaFinDesc(Long usuarioId);

    Optional<Suscripcion> findTopByUsuarioIdAndEstadoOrderByFechaFinDesc(
            Long usuarioId,
            EstadoSuscripcion estado
    );

    List<Suscripcion> findByEstado(EstadoSuscripcion estado);

    @EntityGraph(attributePaths = {"usuario", "plan"})
    Page<Suscripcion> findByEstado(EstadoSuscripcion estado, Pageable pageable);

    @EntityGraph(attributePaths = {"usuario", "plan"})
    Page<Suscripcion> findAll(Pageable pageable);

    List<Suscripcion> findByEstadoAndFechaFinBefore(
            EstadoSuscripcion estado,
            LocalDateTime fechaFin
    );

    List<Suscripcion> findByUsuarioIdInAndEstado(
            List<Long> usuarioIds,
            EstadoSuscripcion estado
    );
}
