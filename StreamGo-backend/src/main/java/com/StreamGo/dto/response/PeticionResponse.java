// Response: lo que se devuelve al cliente cuando elige
package com.StreamGo.dto.response;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
public class PeticionResponse {
    private Long id;
    private Long usuarioId;
    private Long contenidoVotableId;
    private String tituloPelicula;
    private LocalDateTime fechaCreacion;
}
