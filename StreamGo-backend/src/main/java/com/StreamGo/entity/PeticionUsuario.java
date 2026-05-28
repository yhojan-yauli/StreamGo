package com.StreamGo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "peticiones_usuario",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "usuario_id",
                "peticion_id"
        })
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeticionUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // usuario
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // pelicula seleccionada
    @ManyToOne
    @JoinColumn(name = "peticion_id")
    private Peticion peticion;
}