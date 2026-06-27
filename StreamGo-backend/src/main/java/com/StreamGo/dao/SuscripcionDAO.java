package com.StreamGo.dao;

import com.StreamGo.entity.Enum.EstadoSuscripcion;
import com.StreamGo.entity.Suscripcion;
import com.StreamGo.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface SuscripcionDAO extends IGenericDAO<Suscripcion, Long> {

    List<Suscripcion> findByUsuarioId(Long usuarioId);

    List<Suscripcion> findByUsuario(Usuario usuario);

    List<Suscripcion> findByUsuarioIdAndEstado(
            Long usuarioId,
            EstadoSuscripcion estado
    );

    List<Suscripcion> findByUsuarioAndEstado(
            Usuario usuario,
            EstadoSuscripcion estado
    );

    Optional<Suscripcion> findTopByUsuarioIdOrderByFechaFinDesc(
            Long usuarioId
    );

    Optional<Suscripcion> findTopByUsuarioIdAndEstadoOrderByFechaFinDesc(
            Long usuarioId,
            EstadoSuscripcion estado
    );

    List<Suscripcion> findByEstado(EstadoSuscripcion estado);
}
