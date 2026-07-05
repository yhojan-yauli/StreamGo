package com.StreamGo.entity;

import lombok.*;

import java.time.LocalDateTime;

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

    private String portadaUrl;

    private String contenido;

    private LocalDateTime fechaCreacion;

    @Builder.Default
    private boolean fijado = false;
}
