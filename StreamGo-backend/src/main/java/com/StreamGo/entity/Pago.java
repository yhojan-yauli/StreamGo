package com.StreamGo.entity;

import com.StreamGo.entity.Enum.EstadoPago;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {

    private Long id;

    // Usuario que realizó el pago
    private Usuario usuario;

    // Plan comprado
    private Plan plan;

    // Monto pagado
    private BigDecimal monto;

    // Estado del pago
    private EstadoPago estadoPago;

    // ID de Mercado Pago
    private String transactionId;

    // Metodo de pago
    private String metodoPago;

    // Fecha del pago
    private LocalDateTime fechaPago;

    private String mercadoPagoPaymentId;

}
