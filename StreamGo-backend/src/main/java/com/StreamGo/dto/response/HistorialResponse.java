package com.StreamGo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HistorialResponse {

    private Long historialId;

    private Long contenidoId;

    private String titulo;

    private String imagenUrl;

    private String categoria;

    private LocalDateTime fechaReproduccion;

    private Integer progresoSegundos;

    private Boolean completado;
}