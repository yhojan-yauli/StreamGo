package com.StreamGo.entity;

import com.StreamGo.entity.Enum.EstadoContenido;
import com.StreamGo.entity.Enum.TipoContenido;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "contenidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contenido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private String categoria;

    @Enumerated(EnumType.STRING)
    private TipoContenido tipoContenido;

    private String imagenUrl;

    private String bannerUrl;

    private String videoUrl;

    private LocalDate fechaEstreno;

    private Integer duracionMinutos;

    private Boolean gratuito;

    private Boolean recomendado;

    private Boolean tendencia;

    private Double promedioCalificacion;

    private Integer totalCalificaciones;

    private Integer totalReproducciones;

    @Enumerated(EnumType.STRING)
    private EstadoContenido estado;
}