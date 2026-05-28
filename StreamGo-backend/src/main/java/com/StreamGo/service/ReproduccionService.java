package com.StreamGo.service;

import com.StreamGo.dto.response.ReproduccionResponse;
import com.StreamGo.entity.Contenido;
import com.StreamGo.entity.HistorialReproduccion;
import com.StreamGo.entity.Suscripcion;
import com.StreamGo.entity.Usuario;
import com.StreamGo.entity.Enum.EstadoContenido;
import com.StreamGo.entity.Enum.EstadoSuscripcion;
import com.StreamGo.entity.Enum.EstadoUsuario;
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

    public ReproduccionResponse reproducir(
            Long contenidoId,
            String email
    ) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Contenido contenido = contenidoRepository.findById(contenidoId)
                .orElseThrow(() -> new RuntimeException("Contenido no encontrado"));

        if (contenido.getEstado() != EstadoContenido.ACTIVO) {
            throw new RuntimeException("El contenido no está disponible");
        }

        if (usuario.getEstado() != EstadoUsuario.ACTIVO && !Boolean.TRUE.equals(contenido.getGratuito())) {
            throw new RuntimeException("Necesitas una suscripción activa para reproducir este contenido");
        }

        if (!Boolean.TRUE.equals(contenido.getGratuito())) {
            Suscripcion suscripcion = suscripcionRepository.findByUsuario(usuario)
                    .orElseThrow(() -> new RuntimeException("No tienes una suscripción activa"));

            if (suscripcion.getEstado() != EstadoSuscripcion.ACTIVA ||
                    suscripcion.getFechaFin().isBefore(LocalDateTime.now()) ||
                    suscripcion.getHorasRestantes() <= 0) {
                throw new RuntimeException("Tu suscripción no está activa o ya expiró");
            }
        }

        contenido.setTotalReproducciones(
                contenido.getTotalReproducciones() == null
                        ? 1
                        : contenido.getTotalReproducciones() + 1
        );

        contenidoRepository.save(contenido);

        HistorialReproduccion historial = HistorialReproduccion.builder()
                .usuario(usuario)
                .contenido(contenido)
                .fechaReproduccion(LocalDateTime.now())
                .progresoSegundos(0)
                .completado(false)
                .build();

        historialRepository.save(historial);

        return ReproduccionResponse.builder()
                .contenidoId(contenido.getId())
                .titulo(contenido.getTitulo())
                .videoUrl(contenido.getVideoUrl())
                .mensaje("Reproducción iniciada")
                .progresoSegundos(0)
                .completado(false)
                .build();
    }
}