package com.StreamGo.service;

import com.StreamGo.entity.Enum.EstadoSuscripcion;
import com.StreamGo.entity.Enum.EstadoUsuario;
import com.StreamGo.entity.Plan;
import com.StreamGo.entity.Suscripcion;
import com.StreamGo.entity.Usuario;
import com.StreamGo.repository.SuscripcionRepository;
import com.StreamGo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SuscripcionService {

    private final SuscripcionRepository suscripcionRepository;
    private final UsuarioRepository usuarioRepository;

    public Suscripcion crearSuscripcion(
            Usuario usuario,
            Plan plan
    ) {

        LocalDateTime inicio = LocalDateTime.now();

        LocalDateTime fin = inicio.plusHours(
                plan.getDuracionHoras()
        );

        Suscripcion suscripcion = Suscripcion.builder()
                .usuario(usuario)
                .plan(plan)
                .fechaInicio(inicio)
                .fechaFin(fin)
                .horasRestantes(plan.getDuracionHoras())
                .estado(EstadoSuscripcion.ACTIVA)
                .build();

        return suscripcionRepository.save(suscripcion);
    }
    public Suscripcion obtenerSuscripcionUsuario(
            Long usuarioId
    ) {

        return suscripcionRepository
                .findByUsuarioId(usuarioId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Suscripción no encontrada"
                        ));
    }
    public Suscripcion verificarExpiracion(
            Long usuarioId
    ) {

        Suscripcion suscripcion =
                obtenerSuscripcionUsuario(usuarioId);

        if (LocalDateTime.now()
                .isAfter(suscripcion.getFechaFin())) {

            suscripcion.setEstado(
                    EstadoSuscripcion.VENCIDA
            );

            suscripcion.setHorasRestantes(0);

            /*
             * USUARIO INACTIVO
             */

            Usuario usuario = suscripcion.getUsuario();

            usuario.setEstado(
                    EstadoUsuario.INACTIVO
            );

            usuarioRepository.save(usuario);

            return suscripcionRepository.save(
                    suscripcion
            );
        }

        return suscripcion;
    }
}