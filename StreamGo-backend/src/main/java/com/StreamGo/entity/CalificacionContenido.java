package com.StreamGo.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalificacionContenido {

    private Long id;

    // Usuario que califica
    private Usuario usuario;

    // Contenido calificado
    private Contenido contenido;

    // Puntaje del 1 al 5
    private Integer puntaje;

    // Comentario opcional
    private String comentario;

    private LocalDateTime fechaCalificacion;
}
