package com.StreamGo.entity;

import com.StreamGo.entity.Enum.TipoPlan;
import lombok.*;

import java.math.BigDecimal;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class Plan {

        private Long id;

        // Tipo de plan
        private TipoPlan tipoPlan;

        // Nombre visible
        private String nombre;

        // Precio del plan
        private BigDecimal precio;

        // Duración en horas
        private Integer duracionHoras;

        // Descripción
        private String descripcion;

        // Activo o no
        private Boolean activo;

        //plan personalisado
        @Builder.Default
        private Boolean personalizado = false;
    }
