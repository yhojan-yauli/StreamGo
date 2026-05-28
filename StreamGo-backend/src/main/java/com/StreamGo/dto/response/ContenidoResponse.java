package com.StreamGo.dto.response;

import com.StreamGo.entity.Enum.TipoContenido;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ContenidoResponse {

    private Long id;
    private String titulo;
    private String descripcion;
    private String categoria;
    private TipoContenido tipoContenido;
    private String imagenUrl;
    private String bannerUrl;
    private String videoUrl;
    private LocalDate fechaEstreno;
    private Integer duracionMinutos;
    private Boolean gratuito;
    private Boolean recomendado;
    private Boolean tendencia;

    private Double promedioCalificacion;
    private Integer totalCalificaciones;
    private Integer totalReproducciones;
}