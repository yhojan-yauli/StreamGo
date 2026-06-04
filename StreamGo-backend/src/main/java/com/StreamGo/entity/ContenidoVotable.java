package com.StreamGo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "contenidos_votables")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContenidoVotable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private String posterUrl;

    private String imagenUrl;

    private Boolean activo;
}