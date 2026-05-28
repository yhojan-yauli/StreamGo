package com.StreamGo.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReproduccionResponse {

    private Long contenidoId;

    private String titulo;

    private String videoUrl;

    private String mensaje;

    private Integer progresoSegundos;

    private Boolean completado;
}