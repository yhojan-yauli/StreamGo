// Response: lo que se devuelve de una película votable
package com.StreamGo.dto.response;
import lombok.*;

@Data
@Builder
public class ContenidoVotableResponse {
    private Long id;
    private String titulo;
    private String descripcion;
    private String posterUrl;
    private String imagenUrl;
    private Boolean activo;
}
