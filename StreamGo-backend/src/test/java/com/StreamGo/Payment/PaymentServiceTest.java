package com.StreamGo.Payment;


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
import com.StreamGo.service.PaymentService;
import com.StreamGo.service.SuscripcionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - PaymentService")
class PaymentServiceTest {

    @Mock private PagoRepository pRepository;
    @Mock private UsuarioRepository uRepository;
    @Mock private PlanRepository plRepository;
    @Mock private SuscripcionService sService;

    @InjectMocks private PaymentService paymentService;

    @Test
    @DisplayName("Debería procesar pago y activar la cuenta/suscripción del usuario")
    void crearPago_Exito() {
        CrearPagoRequest req = new CrearPagoRequest();
        req.setPlanId(1L);
        req.setMetodoPago("TARJETA");

        Usuario user = Usuario.builder().email("yhojan@streamgo.com").estado(EstadoUsuario.INACTIVO).build();
        Plan plan = Plan.builder().id(1L).nombre("BASICO")
                .precio(java.math.BigDecimal.valueOf(10.0))
                .duracionHoras(20)
                .build();
        Pago pagoMock = Pago.builder().id(50L).estadoPago(EstadoPago.APROBADO).build();
        Suscripcion susMock = Suscripcion.builder().id(99L).horasRestantes(20).build();

        when(uRepository.findByEmail("yhojan@streamgo.com")).thenReturn(Optional.of(user));
        when(plRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(pRepository.save(any(Pago.class))).thenReturn(pagoMock);
        when(sService.crearSuscripcion(user, plan)).thenReturn(susMock);

        PagoResponse res = paymentService.crearPago(req, "yhojan@streamgo.com");

        assertNotNull(res);
        assertEquals(99L, res.getSuscripcionId());
        assertEquals(EstadoUsuario.ACTIVO, user.getEstado());
        verify(pRepository, times(1)).save(any(Pago.class));
    }
}