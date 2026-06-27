package com.StreamGo.entity;

import com.StreamGo.entity.Enum.EstadoContenido;
import com.StreamGo.entity.Enum.TipoContenido;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contenido {

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

    private EstadoContenido estado;
}
