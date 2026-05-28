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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PagoRepository pagoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PlanRepository planRepository;
    private final SuscripcionService suscripcionService;

    public PagoResponse crearPago(
            CrearPagoRequest request,
            String email
    ) {

        Usuario usuario = usuarioRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Usuario no encontrado"
                        ));

        Plan plan = planRepository.findById(
                request.getPlanId()
        ).orElseThrow(() ->
                new RuntimeException(
                        "Plan no encontrado"
                ));

        /*
         * SIMULACIÓN PAGO EXITOSO
         */

        String transactionId =
                UUID.randomUUID().toString();

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

        Pago pagoGuardado =
                pagoRepository.save(pago);

        /*
         * ACTIVAR USUARIO
         */

        usuario.setEstado(
                EstadoUsuario.ACTIVO
        );

        usuarioRepository.save(usuario);

        /*
         * CREAR SUSCRIPCIÓN
         */

        Suscripcion suscripcion =
                suscripcionService
                        .crearSuscripcion(
                                usuario,
                                plan
                        );

        return PagoResponse.builder()
                .pagoId(pagoGuardado.getId())
                .estadoPago(
                        pagoGuardado
                                .getEstadoPago()
                                .name()
                )
                .transactionId(transactionId)
                .suscripcionId(suscripcion.getId())
                .plan(plan.getNombre())
                .fechaInicio(
                        suscripcion.getFechaInicio()
                )
                .fechaFin(
                        suscripcion.getFechaFin()
                )
                .horasRestantes(
                        suscripcion.getHorasRestantes()
                )
                .build();
    }
}