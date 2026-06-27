package com.StreamGo.entity;

import com.StreamGo.entity.Enum.EstadoSuscripcion;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Suscripcion {


    private Long id;

    // Usuario dueño de la suscripción
    private Usuario usuario;

    // Plan comprado
    private Plan plan;

    // Fecha inicio
    private LocalDateTime fechaInicio;

    // Fecha fin
    private LocalDateTime fechaFin;

    // Horas restantes
    private Integer horasRestantes;

    // Estado
    private EstadoSuscripcion estado;
}
