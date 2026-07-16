package com.StreamGo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.StreamGo.dto.response.ReproduccionResponse;
import com.StreamGo.entity.Contenido;
import com.StreamGo.entity.Enum.EstadoContenido;
import com.StreamGo.entity.Enum.EstadoUsuario;
import com.StreamGo.entity.HistorialReproduccion;
import com.StreamGo.entity.Usuario;
import com.StreamGo.repository.ContenidoRepository;
import com.StreamGo.repository.HistorialReproduccionRepository;
import com.StreamGo.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReproduccionService {

    private final UsuarioRepository usuarioRepository;
    private final ContenidoRepository contenidoRepository;
    private final SuscripcionService suscripcionService;
    private final HistorialReproduccionRepository historialRepository;

    public ReproduccionResponse reproducir(Long contenidoId, String email) {
        
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Contenido contenido = contenidoRepository.findById(contenidoId)
                .orElseThrow(() -> new RuntimeException("Contenido no encontrado"));

        log.info("Usuario {} intenta reproducir '{}' (ID={})", email, contenido.getTitulo(), contenidoId);

        if (usuario.getEstado() == EstadoUsuario.SUSPENDIDO) {
            log.warn("Usuario {} suspendido intentó reproducir contenido ID {}", email, contenidoId);
            throw new RuntimeException("Tu cuenta se encuentra suspendida");
        }

        if (contenido.getEstado() == EstadoContenido.ACTIVO) {
            boolean tieneSuscripcion = suscripcionService.tieneSuscripcionActivaSoloLectura(usuario);
            if (!tieneSuscripcion) {
                log.warn("Usuario {} sin suscripción intentó acceder a contenido ACTIVO '{}'", email, contenido.getTitulo());
                throw new RuntimeException("Este contenido requiere una suscripción activa");
            }
        }

        aumentarReproducciones(contenido);

        // Buscar el historial MÁS RECIENTE (con List y tomar el primero)
        List<HistorialReproduccion> historiales = historialRepository.findLastByUsuarioAndContenido(usuario, contenido);
        HistorialReproduccion historial = null;
        
        if (!historiales.isEmpty()) {
            historial = historiales.get(0);
        }

        if (historial == null) {
            historial = HistorialReproduccion.builder()
                    .usuario(usuario)
                    .contenido(contenido)
                    .fechaReproduccion(LocalDateTime.now())
                    .progresoSegundos(0)
                    .completado(false)
                    .build();
        } else {
            historial.setFechaReproduccion(LocalDateTime.now());
        }

        historialRepository.save(historial);

        log.info("Reproducción iniciada correctamente. Usuario: {} | Contenido: {}", email, contenido.getTitulo());

        return construirRespuesta(contenido, "Reproducción iniciada", historial.getProgresoSegundos(), historial.getCompletado());
    }

    public ReproduccionResponse reproducirPublico(Long contenidoId) {
        Contenido contenido = contenidoRepository.findById(contenidoId)
                .orElseThrow(() -> new RuntimeException("Contenido no encontrado"));

        log.info("Intento de reproducción pública del contenido '{}'", contenido.getTitulo());

        if (contenido.getEstado() != EstadoContenido.SINLOGIN) {
            log.warn("Intento de acceso público a contenido restringido '{}'", contenido.getTitulo());
            throw new RuntimeException("Este contenido requiere iniciar sesión");
        }

        aumentarReproducciones(contenido);
        log.info("Reproducción pública iniciada para '{}'", contenido.getTitulo());

        return construirRespuesta(contenido, "Reproducción pública iniciada", 0, false);
    }

    public void actualizarProgreso(Long contenidoId, String email, Integer progresoSegundos, Boolean completado) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Contenido contenido = contenidoRepository.findById(contenidoId)
                .orElseThrow(() -> new RuntimeException("Contenido no encontrado"));

        List<HistorialReproduccion> historiales = historialRepository.findLastByUsuarioAndContenido(usuario, contenido);
        HistorialReproduccion historial = null;
        
        if (!historiales.isEmpty()) {
            historial = historiales.get(0);
        }

        if (historial == null) {
            historial = HistorialReproduccion.builder()
                    .usuario(usuario)
                    .contenido(contenido)
                    .fechaReproduccion(LocalDateTime.now())
                    .progresoSegundos(progresoSegundos != null ? progresoSegundos : 0)
                    .completado(completado != null ? completado : false)
                    .build();
        } else {
            if (progresoSegundos != null) {
                historial.setProgresoSegundos(progresoSegundos);
            }
            if (completado != null) {
                historial.setCompletado(completado);
            }
            historial.setFechaReproduccion(LocalDateTime.now());
        }

        historialRepository.save(historial);
        log.info("Progreso actualizado para usuario {} en contenido {}: {} segundos", 
                email, contenido.getTitulo(), progresoSegundos);
    }

    private void aumentarReproducciones(Contenido contenido) {
        contenido.setTotalReproducciones(
                contenido.getTotalReproducciones() == null
                        ? 1
                        : contenido.getTotalReproducciones() + 1
        );
        contenidoRepository.save(contenido);
    }

    private ReproduccionResponse construirRespuesta(Contenido contenido, String mensaje, Integer progreso, Boolean completado) {
        return ReproduccionResponse.builder()
                .contenidoId(contenido.getId())
                .titulo(contenido.getTitulo())
                .videoUrl(contenido.getVideoUrl())
                .mensaje(mensaje)
                .progresoSegundos(progreso != null ? progreso : 0)
                .completado(completado != null ? completado : false)
                .build();
    }
}