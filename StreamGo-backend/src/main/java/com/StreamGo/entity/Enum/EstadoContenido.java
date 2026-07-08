package com.StreamGo.entity.Enum;

public enum EstadoContenido {
    ACTIVO,     // requiere usuario con suscripción
    INACTIVO,   // visible para usuario logueado sin suscripción
    SINLOGIN    // visible sin iniciar sesión
}