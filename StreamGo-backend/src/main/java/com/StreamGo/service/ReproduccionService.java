package com.StreamGo.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.StreamGo.dao.ContenidoDAO;
import com.StreamGo.dao.HistorialReproduccionDAO;
import com.StreamGo.dao.UsuarioDAO;
import com.StreamGo.dto.response.ReproduccionResponse;
import com.StreamGo.entity.Contenido;
import com.StreamGo.entity.Enum.EstadoContenido;
import com.StreamGo.entity.Enum.EstadoUsuario;
import com.StreamGo.entity.HistorialReproduccion;
import com.StreamGo.entity.Usuario;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio encargado de gestionar la reproducción
 * de contenidos dentro de la plataforma StreamGo.
 *
 * Permite controlar el acceso según el estado
 * del usuario y del contenido.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ReproduccionService {

    private final UsuarioDAO usuarioDAO;
    private final ContenidoDAO contenidoDAO;
    private final SuscripcionService suscripcionService;
    private final HistorialReproduccionDAO historialDAO;

    /**
     * Inicia la reproducción de un contenido para un usuario autenticado.
     *
     * Reglas:
     * - Usuario ACTIVO: acceso total.
     * - Usuario INACTIVO: acceso a contenido INACTIVO y SINLOGIN.
     * - Usuario SUSPENDIDO: acceso denegado.
     *
     * @param contenidoId identificador del contenido.
     * @param email correo del usuario autenticado.
     * @return información de reproducción.
     */
    public ReproduccionResponse reproducir(Long contenidoId, String email) {
        
        Usuario usuario = usuarioDAO.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Contenido contenido = contenidoDAO.findById(contenidoId);

        log.info("Usuario {} intenta reproducir '{}' (ID={})", email, contenido.getTitulo(), contenidoId);

        // Verificar si usuario está suspendido
        if (usuario.getEstado() == EstadoUsuario.SUSPENDIDO) {
            log.warn("Usuario {} suspendido intentó reproducir contenido ID {}", email, contenidoId);
            throw new RuntimeException("Tu cuenta se encuentra suspendida");
        }

        // Verificar acceso premium si el contenido es ACTIVO.
        // Se permite si el usuario está ACTIVO o si tiene una suscripción activa vigente.
        if (contenido.getEstado() == EstadoContenido.ACTIVO && !tieneAccesoPremium(usuario)) {
            log.warn("Usuario {} sin acceso premium intentó acceder a contenido '{}'", email, contenido.getTitulo());
            throw new RuntimeException("Este contenido requiere una suscripción activa");
        }

        // Incrementar reproducciones
        aumentarReproducciones(contenido);

        // Registrar en historial
        HistorialReproduccion historial = HistorialReproduccion.builder()
                .usuario(usuario)
                .contenido(contenido)
                .fechaReproduccion(LocalDateTime.now())
                .progresoSegundos(0)
                .completado(false)
                .build();

        historialDAO.save(historial);

        log.info("Reproducción iniciada correctamente. Usuario: {} | Contenido: {}", email, contenido.getTitulo());

        return construirRespuesta(contenido, "Reproducción iniciada");
    }

    /**
     * Reproduce contenido para usuarios sin iniciar sesión.
     *
     * Solo permite contenidos con estado SINLOGIN.
     *
     * @param contenidoId identificador del contenido público.
     * @return datos de reproducción pública.
     */
    public ReproduccionResponse reproducirPublico(Long contenidoId) {

        Contenido contenido = contenidoDAO.findById(contenidoId);

        log.info("Intento de reproducción pública del contenido '{}'", contenido.getTitulo());

        if (contenido.getEstado() != EstadoContenido.SINLOGIN) {
            log.warn("Intento de acceso público a contenido restringido '{}'", contenido.getTitulo());
            throw new RuntimeException("Este contenido requiere iniciar sesión");
        }

        aumentarReproducciones(contenido);

        log.info("Reproducción pública iniciada para '{}'", contenido.getTitulo());

        return construirRespuesta(contenido, "Reproducción pública iniciada");
    }

    /**
     * Verifica si el usuario posee acceso premium.
     *
     * Se considera acceso premium cuando el usuario está en estado ACTIVO
     * o cuando tiene una suscripción ACTIVA y vigente.
     *
     * @param usuario usuario a validar.
     * @return true si puede acceder a contenido ACTIVO.
     */
    private boolean tieneAccesoPremium(Usuario usuario) {

        if (usuario.getEstado() == EstadoUsuario.ACTIVO) {
            return true;
        }

        return suscripcionService.usuarioTieneSuscripcionActiva(usuario);
    }

    /**
     * Incrementa el contador total de reproducciones de un contenido.
     *
     * @param contenido contenido reproducido.
     */
    private void aumentarReproducciones(Contenido contenido) {
        contenido.setTotalReproducciones(
                contenido.getTotalReproducciones() == null
                        ? 1
                        : contenido.getTotalReproducciones() + 1
        );
        contenidoDAO.update(contenido);
    }

    /**
     * Construye la respuesta de reproducción.
     *
     * @param contenido contenido reproducido.
     * @param mensaje mensaje de respuesta.
     * @return respuesta con datos del contenido reproducido.
     */
    private ReproduccionResponse construirRespuesta(Contenido contenido, String mensaje) {
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
