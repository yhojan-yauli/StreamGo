package com.StreamGo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "historial_reproducciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialReproduccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usuario que reproduce el contenido
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Contenido reproducido
    @ManyToOne
    @JoinColumn(name = "contenido_id")
    private Contenido contenido;

    // Momento en que se reprodujo
    private LocalDateTime fechaReproduccion;

    // Progreso del video en segundos
    private Integer progresoSegundos;

    // Si terminó o no el contenido
    private Boolean completado;
}