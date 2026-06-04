// Request: el admin crea o edita una película votable
package com.StreamGo.dto.request;
import lombok.Data;

@Data
public class ContenidoVotableRequest {
    private String titulo;
    private String descripcion;
    private String posterUrl;
    private String imagenUrl;
}
