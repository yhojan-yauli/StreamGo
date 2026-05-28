package com.StreamGo.dto.request;
import lombok.Data;

@Data
public class CrearPagoRequest {
    private Long planId;
    private String metodoPago;

}