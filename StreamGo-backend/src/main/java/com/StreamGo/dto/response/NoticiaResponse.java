package com.StreamGo.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NoticiaResponse {

    private Long idPost;
    private Long idAutor;
    private String autorNombre;
    private Long idUsuario;
    private String usuarioNombre;
    private String titulo;
    private Integer reacciones;
    private String trailer;
    private String contenido;
}
