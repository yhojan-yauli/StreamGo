package com.StreamGo.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.StreamGo.dto.response.ReproduccionResponse;
import com.StreamGo.entity.Contenido;
import com.StreamGo.entity.Enum.EstadoContenido;
import com.StreamGo.entity.Enum.EstadoSuscripcion;
import com.StreamGo.entity.Enum.EstadoUsuario;
import com.StreamGo.entity.HistorialReproduccion;
import com.StreamGo.entity.Suscripcion;
import com.StreamGo.entity.Usuario;
import com.StreamGo.repository.ContenidoRepository;
import com.StreamGo.repository.HistorialReproduccionRepository;
import com.StreamGo.repository.SuscripcionRepository;
import com.StreamGo.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReproduccionService {

    private final UsuarioRepository usuarioRepository;
    private final ContenidoRepository contenidoRepository;
    private final SuscripcionRepository suscripcionRepository;
    private final HistorialReproduccionRepository historialRepository;

    /*
     * Usuario logueado:
     * - Usuario ACTIVO: puede reproducir ACTIVO, INACTIVO y SINLOGIN.
     * - Usuario INACTIVO: solo puede reproducir INACTIVO y SINLOGIN.
     * - Usuario SUSPENDIDO: no puede reproducir contenido.
     */
    public ReproduccionResponse reproducir(
            Long contenidoId,
            String email
    ) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Contenido contenido = contenidoRepository.findById(contenidoId)
                .orElseThrow(() -> new RuntimeException("Contenido no encontrado"));

        log.info(
                "Usuario {} intenta reproducir '{}' (ID={})",
                email,
                contenido.getTitulo(),
                contenidoId
        );

        if (usuario.getEstado() == EstadoUsuario.SUSPENDIDO) {

            log.warn(
                    "Usuario {} suspendido intentó reproducir contenido ID {}",
                    email,
                    contenidoId
            );

            throw new RuntimeException("Tu cuenta se encuentra suspendida");
        }

        if (usuario.getEstado() == EstadoUsuario.INACTIVO &&
                contenido.getEstado() == EstadoContenido.ACTIVO) {

            log.warn(
                    "Usuario {} sin suscripción intentó acceder a contenido premium '{}'",
                    email,
                    contenido.getTitulo()
            );

            throw new RuntimeException("Este contenido requiere una suscripción activa");
        }

        aumentarReproducciones(contenido);

        HistorialReproduccion historial = HistorialReproduccion.builder()
                .usuario(usuario)
                .contenido(contenido)
                .fechaReproduccion(LocalDateTime.now())
                .progresoSegundos(0)
                .completado(false)
                .build();

        historialRepository.save(historial);

        log.info(
                "Reproducción iniciada correctamente. Usuario: {} | Contenido: {}",
                email,
                contenido.getTitulo()
        );

        return construirRespuesta(
                contenido,
                "Reproducción iniciada"
        );
    }

    /*
     * Usuario sin login:
     * Solo puede reproducir contenido SINLOGIN.
     */
    public ReproduccionResponse reproducirPublico(Long contenidoId) {

        Contenido contenido = contenidoRepository.findById(contenidoId)
                .orElseThrow(() -> new RuntimeException("Contenido no encontrado"));

        log.info(
                "Intento de reproducción pública del contenido '{}'",
                contenido.getTitulo()
        );

        if (contenido.getEstado() != EstadoContenido.SINLOGIN) {

            log.warn(
                    "Intento de acceso público a contenido restringido '{}'",
                    contenido.getTitulo()
            );

            throw new RuntimeException("Este contenido requiere iniciar sesión");
        }

        aumentarReproducciones(contenido);

        log.info(
                "Reproducción pública iniciada para '{}'",
                contenido.getTitulo()
        );

        return construirRespuesta(
                contenido,
                "Reproducción pública iniciada"
        );
    }

    private void validarSuscripcionActiva(Usuario usuario) {

        Suscripcion suscripcion = suscripcionRepository.findByUsuario(usuario)
                .orElseThrow(() ->
                        new RuntimeException("Necesitas una suscripción activa para reproducir este contenido")
                );

        if (suscripcion.getEstado() != EstadoSuscripcion.ACTIVA ||
                suscripcion.getFechaFin().isBefore(LocalDateTime.now()) ||
                suscripcion.getHorasRestantes() <= 0) {

            throw new RuntimeException("Tu suscripción no está activa o ya expiró");
        }
    }

    private void aumentarReproducciones(Contenido contenido) {

        contenido.setTotalReproducciones(
                contenido.getTotalReproducciones() == null
                        ? 1
                        : contenido.getTotalReproducciones() + 1
        );

        contenidoRepository.save(contenido);
    }

    private ReproduccionResponse construirRespuesta(
            Contenido contenido,
            String mensaje
    ) {
        return ReproduccionResponse.builder()
                .contenidoId(contenido.getId())
                .titulo(contenido.getTitulo())
                .videoUrl(contenido.getVideoUrl())
                .mensaje(mensaje)
                .progresoSegundos(0)
                .completado(false)
                .build();
    }
}