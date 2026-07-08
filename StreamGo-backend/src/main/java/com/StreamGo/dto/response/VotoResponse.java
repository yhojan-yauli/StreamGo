// Response: ranking de votos para el admin
package com.StreamGo.dto.response;
import lombok.*;

@Data
@Builder
public class VotoResponse {
    private Long contenidoVotableId;
    private String titulo;
    private Long totalVotos;
}