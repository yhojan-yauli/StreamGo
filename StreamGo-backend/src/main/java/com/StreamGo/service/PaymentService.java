package com.StreamGo.service;

import com.StreamGo.dto.request.CrearPagoRequest;
import com.StreamGo.dto.response.PagoResponse;

import com.StreamGo.entity.Enum.EstadoPago;
import com.StreamGo.entity.Enum.EstadoUsuario;
import com.StreamGo.entity.Pago;
import com.StreamGo.entity.Plan;
import com.StreamGo.entity.Suscripcion;
import com.StreamGo.entity.Usuario;
import com.StreamGo.repository.PagoRepository;
import com.StreamGo.repository.PlanRepository;
import com.StreamGo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Servicio de pagos del sistema StreamGo.
 * Gestiona la creación de pagos, activación de usuarios y generación de suscripciones.
 *
 * @author Yhojan Yauli
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final Logger log =
            LoggerFactory.getLogger(PaymentService.class);

    private final PagoRepository pagoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PlanRepository planRepository;
    private final SuscripcionService suscripcionService;

    /**
     * Procesa un pago y activa la suscripción del usuario.
     * Incluye creación del pago, activación del usuario y generación de suscripción.
     *
     * @param request datos del pago a procesar
     * @param email correo del usuario autenticado
     * @return respuesta con detalle del pago y suscripción generada
     */
    public PagoResponse crearPago(
            CrearPagoRequest request,
            String email
    ) {

        log.info("Iniciando proceso de pago para email: {}", email);

        Usuario usuario = usuarioRepository
                .findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Usuario no encontrado: {}", email);
                    return new RuntimeException("Usuario no encontrado");
                });

        log.debug("Usuario encontrado: {}", usuario.getEmail());

        Plan plan = planRepository.findById(
                request.getPlanId()
        ).orElseThrow(() -> {
            log.error("Plan no encontrado ID: {}", request.getPlanId());
            return new RuntimeException("Plan no encontrado");
        });

        log.debug("Plan seleccionado: {} - S/{}", plan.getNombre(), plan.getPrecio());

        String transactionId = UUID.randomUUID().toString();

        log.info("Generando transacción: {}", transactionId);

        Pago pago = Pago.builder()
                .usuario(usuario)
                .plan(plan)
                .monto(plan.getPrecio())
                .estadoPago(EstadoPago.APROBADO)
                .transactionId(transactionId)
                .metodoPago(request.getMetodoPago())
                .fechaPago(LocalDateTime.now())
                .mercadoPagoPaymentId("SIMULATED")
                .build();

        Pago pagoGuardado = pagoRepository.save(pago);

        log.info("Pago guardado con ID: {}", pagoGuardado.getId());

        usuario.setEstado(EstadoUsuario.ACTIVO);
        usuarioRepository.save(usuario);

        log.info("Usuario activado: {}", usuario.getEmail());

        Suscripcion suscripcion =
                suscripcionService.crearSuscripcion(usuario, plan);

        log.info("Suscripción creada ID: {}", suscripcion.getId());

        return PagoResponse.builder()
                .pagoId(pagoGuardado.getId())
                .estadoPago(pagoGuardado.getEstadoPago().name())
                .transactionId(transactionId)
                .suscripcionId(suscripcion.getId())
                .plan(plan.getNombre())
                .fechaInicio(suscripcion.getFechaInicio())
                .fechaFin(suscripcion.getFechaFin())
                .horasRestantes(suscripcion.getHorasRestantes())
                .build();
    }
}