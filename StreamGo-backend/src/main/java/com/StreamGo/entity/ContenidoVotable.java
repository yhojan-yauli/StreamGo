package com.StreamGo.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContenidoVotable {

    private Long id;

    private String titulo;

    private String descripcion;

    private String posterUrl;

    private String imagenUrl;

    private Boolean activo;
}
