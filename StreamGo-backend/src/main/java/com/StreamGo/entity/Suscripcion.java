package com.StreamGo.entity;

import com.StreamGo.entity.Enum.EstadoSuscripcion;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "suscripciones")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Suscripcion {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usuario dueño de la suscripción
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Plan comprado
    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plan plan;

    // Fecha inicio
    private LocalDateTime fechaInicio;

    // Fecha fin
    private LocalDateTime fechaFin;

    // Horas restantes
    private Integer horasRestantes;

    // Estado
    @Enumerated(EnumType.STRING)
    private EstadoSuscripcion estado;
}