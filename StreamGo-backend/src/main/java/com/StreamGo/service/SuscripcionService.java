package com.StreamGo.service;

import com.StreamGo.entity.Enum.EstadoSuscripcion;
import com.StreamGo.entity.Enum.EstadoUsuario;
import com.StreamGo.entity.Plan;
import com.StreamGo.entity.Suscripcion;
import com.StreamGo.entity.Usuario;
import com.StreamGo.repository.SuscripcionRepository;
import com.StreamGo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de gestión de suscripciones del sistema StreamGo.
 * Maneja creación, consulta, validación y expiración automática de suscripciones.
 *
 * @author Yhojan Yauli
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class SuscripcionService {

    private static final Logger logger =
            LoggerFactory.getLogger(SuscripcionService.class);

    private final SuscripcionRepository suscripcionRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Obtiene el usuario actualmente autenticado mediante JWT.
     *
     * @return usuario logueado en el sistema
     */
    public Usuario obtenerUsuarioLogueado() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        logger.debug("Usuario autenticado: {}", email);

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Usuario no encontrado con email: {}", email);
                    return new RuntimeException("Usuario no encontrado");
                });
    }

    /**
     * Crea una nueva suscripción para el usuario autenticado.
     *
     * @param plan plan seleccionado por el usuario
     * @return suscripción creada
     */
    public Suscripcion crearSuscripcion(Plan plan) {

        Usuario usuario = obtenerUsuarioLogueado();

        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fin = inicio.plusHours(plan.getDuracionHoras());

        logger.info("Creando suscripción para usuario: {} con plan: {}",
                usuario.getEmail(), plan.getNombre());

        Suscripcion suscripcion = Suscripcion.builder()
                .usuario(usuario)
                .plan(plan)
                .fechaInicio(inicio)
                .fechaFin(fin)
                .horasRestantes(plan.getDuracionHoras())
                .estado(EstadoSuscripcion.ACTIVA)
                .build();

        Suscripcion guardada = suscripcionRepository.save(suscripcion);

        logger.info("Suscripción creada correctamente ID: {}", guardada.getId());

        return guardada;
    }

    /**
     * Obtiene la suscripción del usuario autenticado.
     *
     * @return suscripción del usuario
     */
    public Suscripcion obtenerSuscripcionUsuario() {

        Usuario usuario = obtenerUsuarioLogueado();

        logger.debug("Obteniendo suscripción del usuario ID: {}", usuario.getId());

        return suscripcionRepository
                .findByUsuarioId(usuario.getId())
                .orElseThrow(() -> {
                    logger.warn("No se encontró suscripción para usuario ID: {}", usuario.getId());
                    return new RuntimeException("Suscripción no encontrada");
                });
    }

    /**
     * Verifica manualmente si la suscripción del usuario ha expirado.
     * Actualiza estado de usuario y suscripción si corresponde.
     *
     * @return suscripción actualizada
     */
    public Suscripcion verificarExpiracion() {

        Usuario usuario = obtenerUsuarioLogueado();

        Suscripcion suscripcion =
                obtenerSuscripcionUsuario();

        if (LocalDateTime.now().isAfter(suscripcion.getFechaFin())) {

            logger.warn("Suscripción vencida para usuario ID: {}", usuario.getId());

            suscripcion.setEstado(EstadoSuscripcion.VENCIDA);
            suscripcion.setHorasRestantes(0);

            usuario.setEstado(EstadoUsuario.INACTIVO);

            usuarioRepository.save(usuario);
            suscripcionRepository.save(suscripcion);

            return suscripcion;
        }

        long horas = calcularHorasRestantes(suscripcion);
        suscripcion.setHorasRestantes((int) horas);

        logger.debug("Horas restantes actualizadas: {}", horas);

        return suscripcion;
    }

    /**
     * Calcula las horas restantes de una suscripción activa.
     *
     * @param suscripcion suscripción a evaluar
     * @return horas restantes o 0 si ya expiró
     */
    public long calcularHorasRestantes(Suscripcion suscripcion) {

        if (LocalDateTime.now().isAfter(suscripcion.getFechaFin())) {
            return 0;
        }

        return java.time.Duration
                .between(LocalDateTime.now(), suscripcion.getFechaFin())
                .toHours();
    }

    /**
     * Ejecuta una verificación automática de suscripciones expiradas.
     * Se ejecuta periódicamente mediante scheduler.
     */
    @Scheduled(fixedRate = 600000)
    public void actualizarSuscripcionesExpiradas() {

        logger.info("Ejecutando verificación de suscripciones...");

        List<Suscripcion> suscripciones =
                suscripcionRepository.findAll();

        LocalDateTime ahora = LocalDateTime.now();

        for (Suscripcion s : suscripciones) {

            if (s.getEstado() == EstadoSuscripcion.ACTIVA &&
                    ahora.isAfter(s.getFechaFin())) {

                s.setEstado(EstadoSuscripcion.VENCIDA);
                s.setHorasRestantes(0);

                Usuario usuario = s.getUsuario();
                usuario.setEstado(EstadoUsuario.INACTIVO);

                usuarioRepository.save(usuario);
                suscripcionRepository.save(s);

                logger.warn(" Suscripción vencida ID: {}", s.getId());
            }
        }
    }
}