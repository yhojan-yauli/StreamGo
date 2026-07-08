package com.StreamGo.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    private String nombre;
    private String email;
    private String password;
}