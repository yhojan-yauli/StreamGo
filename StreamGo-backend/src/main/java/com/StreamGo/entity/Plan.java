package com.StreamGo.entity;

import com.StreamGo.entity.Enum.TipoPlan;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;


    @Entity
    @Table(name = "planes")

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class Plan {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        // Tipo de plan
        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private TipoPlan tipoPlan;

        // Nombre visible
        @Column(nullable = false)
        private String nombre;

        // Precio del plan
        @Column(nullable = false)
        private BigDecimal precio;

        // Duración en horas
        @Column(nullable = false)
        private Integer duracionHoras;

        // Descripción
        private String descripcion;

        // Activo o no
        private Boolean activo;
    }
