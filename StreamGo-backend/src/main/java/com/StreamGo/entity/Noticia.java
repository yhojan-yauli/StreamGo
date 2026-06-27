package com.StreamGo.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Noticia {

    private Long idPost;

    private Usuario autor;

    private Usuario usuario;

    private String titulo;

    private Integer reacciones;

    private String trailer;

    private String contenido;

    @Builder.Default
    private boolean fijado = false;
}
