package com.StreamGo.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeticionUsuario {

    private Long id;

    // usuario
    private Usuario usuario;

    // pelicula seleccionada
    private Peticion peticion;
}
