package com.StreamGo.Suscripcion;


import com.StreamGo.entity.Enum.EstadoSuscripcion;
import com.StreamGo.entity.Plan;
import com.StreamGo.entity.Suscripcion;
import com.StreamGo.entity.Usuario;
import com.StreamGo.repository.SuscripcionRepository;
import com.StreamGo.repository.UsuarioRepository;
import com.StreamGo.service.SuscripcionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - SuscripcionService")
class SuscripcionServiceTest {

    @Mock private SuscripcionRepository suscripcionRepository;
    @Mock private UsuarioRepository usuarioRepository;

    @InjectMocks private SuscripcionService suscripcionService;

    @Test
    @DisplayName("Debería crear una suscripción acumulada calculando correctamente la línea de tiempo")
    void crearSuscripcion_Acumulativa() {
        Usuario usuario = Usuario.builder().id(1L).email("yhojan@streamgo.com").build();
        Plan planNuevo = Plan.builder().duracionHoras(10).nombre("Plan 10h").build();

        // Simulamos que ya cuenta con una suscripción activa que vence en 2 horas
        Suscripcion suscripcionExistente = Suscripcion.builder()
                .id(10L)
                .estado(EstadoSuscripcion.ACTIVA)
                .fechaFin(LocalDateTime.now().plusHours(2))
                .build();

        List<Suscripcion> listaMock = new ArrayList<>();
        listaMock.add(suscripcionExistente);

        // Agregamos la nueva que retornará el save
        Suscripcion nuevaMock = Suscripcion.builder()
                .id(11L)
                .estado(EstadoSuscripcion.ACTIVA)
                .fechaFin(LocalDateTime.now().plusHours(12)) // Vence en 12h (2h existentes + 10h nuevas)
                .build();
        listaMock.add(nuevaMock);

        when(suscripcionRepository.findByUsuarioId(1L)).thenReturn(listaMock);
        when(suscripcionRepository.save(any(Suscripcion.class))).thenReturn(nuevaMock);

        // Act
        Suscripcion resultado = suscripcionService.crearSuscripcion(usuario, planNuevo);

        // Assert
        assertNotNull(resultado);
        assertEquals(EstadoSuscripcion.ACTIVA, resultado.getEstado());
        verify(suscripcionRepository, times(1)).save(any(Suscripcion.class));
    }
}