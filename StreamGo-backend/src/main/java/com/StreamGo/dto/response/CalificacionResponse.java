package com.StreamGo.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CalificacionResponse {

    private Long contenidoId;

    private String titulo;

    private Integer puntaje;

    private String comentario;

    private Double promedioCalificacion;

    private Integer totalCalificaciones;

    private String mensaje;
}