package com.StreamGo.dto.request;

import java.time.LocalDate;

import com.StreamGo.entity.Enum.EstadoContenido;
import com.StreamGo.entity.Enum.TipoContenido;

import lombok.Data;

@Data
public class ActualizarContenidoRequest {

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
    private EstadoContenido estado;
}