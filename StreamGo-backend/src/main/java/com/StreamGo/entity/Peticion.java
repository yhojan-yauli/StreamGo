package com.StreamGo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "contenido_votable_id", nullable = false)
    private ContenidoVotable contenidoVotable;

    private LocalDateTime fechaPeticion;
}
