package com.StreamGo.entity;

import com.StreamGo.entity.Enum.EstadoPago;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usuario que realizó el pago
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Plan comprado
    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plan plan;

    // Monto pagado
    private BigDecimal monto;

    // Estado del pago
    @Enumerated(EnumType.STRING)
    private EstadoPago estadoPago;

    // ID de Mercado Pago
    private String transactionId;

    // Metodo de pago
    private String metodoPago;

    // Fecha del pago
    private LocalDateTime fechaPago;

    private String mercadoPagoPaymentId;

}