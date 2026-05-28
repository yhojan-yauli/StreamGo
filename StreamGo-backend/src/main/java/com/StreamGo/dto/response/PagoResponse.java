package com.StreamGo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PagoResponse {

    private Long pagoId;
    private String estadoPago;
    private String transactionId;
    private Long suscripcionId;
    private String plan;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Integer horasRestantes;

}