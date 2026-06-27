package com.StreamGo.entity;

import com.StreamGo.entity.Enum.EstadoUsuario;
import com.StreamGo.entity.Enum.Rol;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    private Long id;

    private String nombre;

    private String email;

    private String password;

    private String telefono;

    private String avatar;

    private Rol rol;

    private EstadoUsuario estado;

    private LocalDateTime fechaRegistro;

    private LocalDateTime ultimoAcceso;
}
