package com.StreamGo.repository;

import com.StreamGo.entity.Enum.EstadoSuscripcion;
import com.StreamGo.entity.Suscripcion;
import com.StreamGo.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SuscripcionRepository
        extends JpaRepository<Suscripcion, Long> {

    Optional<Suscripcion> findByUsuarioId(Long usuarioId);
    Optional<Suscripcion> findByUsuario(Usuario usuario);
    List<Suscripcion> findByEstado(EstadoSuscripcion estado);

}