package com.StreamGo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "calificaciones_contenido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalificacionContenido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usuario que califica
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Contenido calificado
    @ManyToOne
    @JoinColumn(name = "contenido_id")
    private Contenido contenido;

    // Puntaje del 1 al 5
    private Integer puntaje;

    // Comentario opcional
    private String comentario;

    private LocalDateTime fechaCalificacion;
}