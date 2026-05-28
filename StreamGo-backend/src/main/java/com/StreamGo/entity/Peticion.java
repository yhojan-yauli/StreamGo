package com.StreamGo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "peticiones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Peticion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // titulo de la pelicula
    @Column(nullable = false)
    private String titulo;

    // descripcion de la pelicula
    @Column(length = 1000)
    private String descripcion;

    // imagen/poster
    private String imagenUrl;
}
