package com.StreamGo.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClienteAdminResponse {

    private String avatar;

    private String nombre;

    private String estado;

    private String email;

    private String telefono;

    private String ultimoAcceso;

    private Boolean tieneSuscripcion;

    private Long horasRestantes;
}