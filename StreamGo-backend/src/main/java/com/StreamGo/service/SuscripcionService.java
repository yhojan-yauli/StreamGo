package com.StreamGo.service;

import com.StreamGo.dao.SuscripcionDAO;
import com.StreamGo.dao.UsuarioDAO;
import com.StreamGo.entity.Enum.EstadoSuscripcion;
import com.StreamGo.entity.Enum.EstadoUsuario;
import com.StreamGo.entity.Plan;
import com.StreamGo.entity.Suscripcion;
import com.StreamGo.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * Servicio de gestión de suscripciones del sistema StreamGo.
 *
 * La duración del plan se maneja por tiempo real de reloj.
 * Ejemplo: si el usuario compra un plan de 10 horas, la suscripción vence
 * 10 horas después de la compra, no según la cantidad de horas que vea películas.
 *
 * Si el usuario compra otro plan mientras aún tiene tiempo vigente,
 * el nuevo plan se acumula al final del tiempo actual.
 */
@Service
@RequiredArgsConstructor
public class SuscripcionService {

    private static final Logger logger =
            LoggerFactory.getLogger(SuscripcionService.class);

    private final SuscripcionDAO suscripcionDAO;
    private final UsuarioDAO usuarioDAO;

    /**
     * Obtiene el usuario actualmente autenticado mediante JWT.
     *
     * @return usuario logueado en el sistema.
     */
    public Usuario obtenerUsuarioLogueado() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        logger.debug("Usuario autenticado: {}", email);

        return usuarioDAO.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Usuario no encontrado con email: {}", email);
                    return new RuntimeException("Usuario no encontrado");
                });
    }

    /**
     * Crea una suscripción para el usuario autenticado.
     *
     * @param plan plan seleccionado por el usuario.
     * @return suscripción acumulada actual del usuario.
     */
    @Transactional
    public Suscripcion crearSuscripcion(Plan plan) {
        Usuario usuario = obtenerUsuarioLogueado();
        return crearSuscripcion(usuario, plan);
    }

    /**
     * Crea una suscripción para un usuario específico.
     *
     * Regla importante:
     * - Si el usuario no tiene suscripción vigente, el plan empieza ahora.
     * - Si el usuario ya tiene tiempo vigente, el nuevo plan empieza cuando termine
     *   el tiempo actual, acumulando la duración.
     *
     * Ejemplo:
     * - Compra 10 horas a las 10:00 -> vence a las 20:00.
     * - Compra otras 10 horas a las 10:05 -> se acumula y vence a las 06:00 del día siguiente.
     *
     * @param usuario usuario que adquiere el plan.
     * @param plan plan seleccionado.
     * @return suscripción acumulada actual del usuario.
     */
    @Transactional
    public Suscripcion crearSuscripcion(Usuario usuario, Plan plan) {

        verificarExpiracionUsuario(usuario);

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime inicio = obtenerFechaFinMasLejanaVigente(usuario);

        if (inicio == null || inicio.isBefore(ahora)) {
            inicio = ahora;
        }

        LocalDateTime fin = inicio.plusHours(plan.getDuracionHoras());

        logger.info(
                "Creando suscripción para usuario: {} con plan: {}",
                usuario.getEmail(),
                plan.getNombre()
        );

        Suscripcion nuevaSuscripcion = Suscripcion.builder()
                .usuario(usuario)
                .plan(plan)
                .fechaInicio(inicio)
                .fechaFin(fin)
                .horasRestantes(plan.getDuracionHoras())
                .estado(EstadoSuscripcion.ACTIVA)
                .build();

        suscripcionDAO.save(nuevaSuscripcion);

        usuario.setEstado(EstadoUsuario.ACTIVO);
        usuarioDAO.update(usuario);

        logger.info("Suscripción creada correctamente ID: {}", nuevaSuscripcion.getId());

        return construirSuscripcionAcumulada(usuario);
    }

    /**
     * Obtiene la suscripción acumulada del usuario autenticado.
     *
     * @return resumen de suscripción vigente del usuario.
     */
    public Suscripcion obtenerSuscripcionUsuario() {

        Usuario usuario = obtenerUsuarioLogueado();

        logger.debug("Obteniendo suscripciones del usuario ID: {}", usuario.getId());

        verificarExpiracionUsuario(usuario);

        return construirSuscripcionAcumulada(usuario);
    }

    /**
     * Verifica la expiración del usuario autenticado y devuelve su suscripción vigente.
     *
     * @return suscripción acumulada actualizada.
     */
    @Transactional
    public Suscripcion verificarExpiracion() {

        Usuario usuario = obtenerUsuarioLogueado();

        verificarExpiracionUsuario(usuario);

        return construirSuscripcionAcumulada(usuario);
    }

    /**
     * Indica si un usuario tiene tiempo de suscripción vigente.
     *
     * @param usuario usuario a validar.
     * @return true si tiene suscripción activa por tiempo real.
     */
    @Transactional
    public boolean usuarioTieneSuscripcionActiva(Usuario usuario) {
        return calcularHorasRestantesTotales(usuario) > 0;
    }

    /**
     * Calcula las horas restantes usando la fecha de vencimiento más lejana.
     *
     * No descuenta horas por reproducción de películas.
     * Solo cuenta el tiempo real entre ahora y la fecha fin de la suscripción acumulada.
     *
     * @param usuario usuario a evaluar.
     * @return horas restantes por reloj.
     */
    @Transactional
    public long calcularHorasRestantesTotales(Usuario usuario) {

        verificarExpiracionUsuario(usuario);

        LocalDateTime fechaFinMasLejana = obtenerFechaFinMasLejanaVigente(usuario);

        if (fechaFinMasLejana == null) {
            return 0;
        }

        return calcularHorasRestantesHasta(fechaFinMasLejana);
    }

    /**
     * Calcula las horas restantes de una suscripción individual por reloj.
     *
     * @param suscripcion suscripción a evaluar.
     * @return horas restantes o 0 si ya expiró.
     */
    public long calcularHorasRestantes(Suscripcion suscripcion) {

        if (suscripcion.getFechaFin() == null) {
            return 0;
        }

        return calcularHorasRestantesHasta(suscripcion.getFechaFin());
    }

    /**
     * Construye una respuesta resumen de la suscripción acumulada.
     *
     * La respuesta muestra:
     * - fechaInicio: inicio del primer tramo vigente.
     * - fechaFin: vencimiento final acumulado.
     * - horasRestantes: horas reales restantes hasta fechaFin.
     *
     * @param usuario usuario dueño de las suscripciones.
     * @return suscripción resumen acumulada.
     */
    private Suscripcion construirSuscripcionAcumulada(Usuario usuario) {

        List<Suscripcion> todas = suscripcionDAO.findByUsuarioId(usuario.getId());

        if (todas.isEmpty()) {
            throw new RuntimeException("Suscripción no encontrada");
        }

        LocalDateTime ahora = LocalDateTime.now();

        List<Suscripcion> vigentes = todas.stream()
                .filter(s -> s.getEstado() == EstadoSuscripcion.ACTIVA)
                .filter(s -> s.getFechaFin() != null)
                .filter(s -> s.getFechaFin().isAfter(ahora))
                .toList();

        if (vigentes.isEmpty()) {
            Suscripcion ultima = obtenerUltimaSuscripcion(todas);
            ultima.setHorasRestantes(0);
            ultima.setEstado(EstadoSuscripcion.VENCIDA);
            return ultima;
        }

        Suscripcion referencia = obtenerUltimaSuscripcion(vigentes);

        LocalDateTime fechaInicio = vigentes.stream()
                .map(Suscripcion::getFechaInicio)
                .filter(fecha -> fecha != null)
                .min(LocalDateTime::compareTo)
                .orElse(ahora);

        long horasRestantes = calcularHorasRestantesHasta(referencia.getFechaFin());

        return Suscripcion.builder()
                .id(referencia.getId())
                .usuario(usuario)
                .plan(referencia.getPlan())
                .fechaInicio(fechaInicio)
                .fechaFin(referencia.getFechaFin())
                .horasRestantes((int) horasRestantes)
                .estado(EstadoSuscripcion.ACTIVA)
                .build();
    }

    /**
     * Verifica las suscripciones vencidas de un usuario específico.
     *
     * @param usuario usuario a verificar.
     */
    private void verificarExpiracionUsuario(Usuario usuario) {

        LocalDateTime ahora = LocalDateTime.now();

        List<Suscripcion> suscripciones =
                suscripcionDAO.findByUsuarioId(usuario.getId());

        for (Suscripcion suscripcion : suscripciones) {
            if (suscripcion.getEstado() == EstadoSuscripcion.ACTIVA &&
                    suscripcion.getFechaFin() != null &&
                    !suscripcion.getFechaFin().isAfter(ahora)) {

                suscripcion.setEstado(EstadoSuscripcion.VENCIDA);
                suscripcion.setHorasRestantes(0);
                suscripcionDAO.update(suscripcion);

                logger.warn("Suscripción vencida ID: {}", suscripcion.getId());
            }
        }

        LocalDateTime fechaFinMasLejana = obtenerFechaFinMasLejanaVigente(usuario);

        if (fechaFinMasLejana != null && fechaFinMasLejana.isAfter(ahora)) {
            usuario.setEstado(EstadoUsuario.ACTIVO);
        } else if (usuario.getEstado() != EstadoUsuario.SUSPENDIDO) {
            usuario.setEstado(EstadoUsuario.INACTIVO);
        }

        usuarioDAO.update(usuario);
    }

    /**
     * Obtiene la fecha de vencimiento más lejana entre las suscripciones activas.
     *
     * @param usuario usuario a evaluar.
     * @return fecha fin más lejana o null si no hay suscripción vigente.
     */
    private LocalDateTime obtenerFechaFinMasLejanaVigente(Usuario usuario) {

        LocalDateTime ahora = LocalDateTime.now();

        return suscripcionDAO
                .findByUsuarioIdAndEstado(usuario.getId(), EstadoSuscripcion.ACTIVA)
                .stream()
                .map(Suscripcion::getFechaFin)
                .filter(fechaFin -> fechaFin != null)
                .filter(fechaFin -> fechaFin.isAfter(ahora))
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    /**
     * Calcula horas restantes usando redondeo hacia arriba.
     *
     * Así, justo después de comprar un plan de 10 horas, Swagger mostrará 10
     * y no 9 por diferencia de segundos o milisegundos.
     *
     * @param fechaFin fecha final a evaluar.
     * @return horas restantes redondeadas hacia arriba.
     */
    private long calcularHorasRestantesHasta(LocalDateTime fechaFin) {

        LocalDateTime ahora = LocalDateTime.now();

        if (fechaFin == null || !fechaFin.isAfter(ahora)) {
            return 0;
        }

        long segundos = Duration.between(ahora, fechaFin).getSeconds();

        return (segundos + 3599) / 3600;
    }

    /**
     * Obtiene la última suscripción tomando primero fecha fin y luego ID.
     *
     * @param suscripciones lista de suscripciones.
     * @return última suscripción encontrada.
     */
    private Suscripcion obtenerUltimaSuscripcion(List<Suscripcion> suscripciones) {
        return suscripciones.stream()
                .max(
                        Comparator
                                .comparing(
                                        Suscripcion::getFechaFin,
                                        Comparator.nullsLast(LocalDateTime::compareTo)
                                )
                                .thenComparing(
                                        Suscripcion::getId,
                                        Comparator.nullsLast(Long::compareTo)
                                )
                )
                .orElseThrow(() -> new RuntimeException("Suscripción no encontrada"));
    }

    /**
     * Ejecuta una verificación automática de suscripciones expiradas.
     */
    @Scheduled(fixedRate = 600000)
    @Transactional
    public void actualizarSuscripcionesExpiradas() {

        logger.info("Ejecutando verificación de suscripciones...");

        List<Suscripcion> suscripciones = suscripcionDAO.findAll();
        LocalDateTime ahora = LocalDateTime.now();

        for (Suscripcion suscripcion : suscripciones) {

            if (suscripcion.getEstado() == EstadoSuscripcion.ACTIVA &&
                    suscripcion.getFechaFin() != null &&
                    !suscripcion.getFechaFin().isAfter(ahora)) {

                suscripcion.setEstado(EstadoSuscripcion.VENCIDA);
                suscripcion.setHorasRestantes(0);
                suscripcionDAO.update(suscripcion);

                Usuario usuario = suscripcion.getUsuario();

                if (usuario != null && !usuarioTieneSuscripcionActiva(usuario)) {
                    if (usuario.getEstado() != EstadoUsuario.SUSPENDIDO) {
                        usuario.setEstado(EstadoUsuario.INACTIVO);
                        usuarioDAO.update(usuario);
                    }
                }

                logger.warn("Suscripción vencida ID: {}", suscripcion.getId());
            }
        }
    }
}
