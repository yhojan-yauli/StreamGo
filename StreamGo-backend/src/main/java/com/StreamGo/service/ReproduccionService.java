package com.StreamGo.service;

import com.StreamGo.dto.response.ReproduccionResponse;
import com.StreamGo.entity.Contenido;
import com.StreamGo.entity.HistorialReproduccion;
import com.StreamGo.entity.Suscripcion;
import com.StreamGo.entity.Usuario;
import com.StreamGo.entity.Enum.EstadoContenido;
import com.StreamGo.entity.Enum.EstadoSuscripcion;
import com.StreamGo.repository.ContenidoRepository;
import com.StreamGo.repository.HistorialReproduccionRepository;
import com.StreamGo.repository.SuscripcionRepository;
import com.StreamGo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReproduccionService {

    private final UsuarioRepository usuarioRepository;
    private final ContenidoRepository contenidoRepository;
    private final SuscripcionRepository suscripcionRepository;
    private final HistorialReproduccionRepository historialRepository;

    /*
     * Usuario logueado:
     * - SINLOGIN: permitido
     * - INACTIVO: permitido sin suscripción
     * - ACTIVO: requiere suscripción activa
     */
    public ReproduccionResponse reproducir(
            Long contenidoId,
            String email
    ) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Contenido contenido = contenidoRepository.findById(contenidoId)
                .orElseThrow(() -> new RuntimeException("Contenido no encontrado"));

        if (contenido.getEstado() == EstadoContenido.ACTIVO) {
            validarSuscripcionActiva(usuario);
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

        if (contenido.getEstado() != EstadoContenido.SINLOGIN) {
            throw new RuntimeException("Este contenido requiere iniciar sesión");
        }

        aumentarReproducciones(contenido);

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