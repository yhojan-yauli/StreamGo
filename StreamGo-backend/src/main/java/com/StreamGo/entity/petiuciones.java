package com.StreamGo.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "peticiones",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"usuario_id", "pelicula"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class petiuciones {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // usuario que votó
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // película elegida
    @Column(nullable = false)
    private String pelicula;
}
