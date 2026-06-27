package com.StreamGo.entity;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Peticion {

    private Long id;

    private Usuario usuario;

    private ContenidoVotable contenidoVotable;

    private LocalDateTime fechaPeticion;
}
