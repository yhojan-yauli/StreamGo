package com.StreamGo.dto.request;

import lombok.Data;

@Data
public class NoticiaRequest {

    private Long idAutor;
    private Long idUsuario;
    private String titulo;
    private Integer reacciones;
    private String trailer;
    private String contenido;
}
