package com.StreamGo.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialReproduccion {

    private Long id;

    // Usuario que reproduce el contenido
    private Usuario usuario;

    // Contenido reproducido
    private Contenido contenido;

    // Momento en que se reprodujo
    private LocalDateTime fechaReproduccion;

    // Progreso del video en segundos
    private Integer progresoSegundos;

    // Si terminó o no el contenido
    private Boolean completado;
}
