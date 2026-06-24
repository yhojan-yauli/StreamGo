package com.StreamGo.service;

import com.StreamGo.dto.request.CalificacionRequest;
import com.StreamGo.dto.response.CalificacionResponse;
import com.StreamGo.entity.CalificacionContenido;
import com.StreamGo.entity.Contenido;
import com.StreamGo.entity.Usuario;
import com.StreamGo.repository.CalificacionRepository;
import com.StreamGo.repository.ContenidoRepository;
import com.StreamGo.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalificacionServiceTest {

    @Mock
    private CalificacionRepository calificacionRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ContenidoRepository contenidoRepository;

    @InjectMocks
    private CalificacionService calificacionService;

    @Test
    void debeCrearNuevaCalificacionYActualizarPromedio() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .email("cliente@gmail.com")
                .build();

        Contenido contenido = Contenido.builder()
                .id(1L)
                .titulo("Naruto")
                .promedioCalificacion(0.0)
                .totalCalificaciones(0)
                .build();

        CalificacionRequest request = new CalificacionRequest();
        request.setPuntaje(5);
        request.setComentario("Muy bueno");

        CalificacionContenido calificacion = CalificacionContenido.builder()
                .id(1L)
                .usuario(usuario)
                .contenido(contenido)
                .puntaje(5)
                .comentario("Muy bueno")
                .build();

        when(usuarioRepository.findByEmail("cliente@gmail.com")).thenReturn(Optional.of(usuario));
        when(contenidoRepository.findById(1L)).thenReturn(Optional.of(contenido));
        when(calificacionRepository.findByUsuarioAndContenido(usuario, contenido)).thenReturn(Optional.empty());
        when(calificacionRepository.save(any(CalificacionContenido.class))).thenReturn(calificacion);
        when(calificacionRepository.findByContenido(contenido)).thenReturn(List.of(calificacion));

        CalificacionResponse response =
                calificacionService.calificarContenido(1L, "cliente@gmail.com", request);

        assertEquals(5, response.getPuntaje());
        assertEquals("Naruto", response.getTitulo());
        assertEquals(5.0, response.getPromedioCalificacion());
        assertEquals(1, response.getTotalCalificaciones());
        verify(contenidoRepository).save(contenido);
    }

    @Test
    void debeActualizarCalificacionExistente() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .email("cliente@gmail.com")
                .build();

        Contenido contenido = Contenido.builder()
                .id(1L)
                .titulo("Naruto")
                .promedioCalificacion(3.0)
                .totalCalificaciones(1)
                .build();

        CalificacionContenido existente = CalificacionContenido.builder()
                .id(1L)
                .usuario(usuario)
                .contenido(contenido)
                .puntaje(3)
                .comentario("Regular")
                .build();

        CalificacionRequest request = new CalificacionRequest();
        request.setPuntaje(4);
        request.setComentario("Mejoró");

        when(usuarioRepository.findByEmail("cliente@gmail.com")).thenReturn(Optional.of(usuario));
        when(contenidoRepository.findById(1L)).thenReturn(Optional.of(contenido));
        when(calificacionRepository.findByUsuarioAndContenido(usuario, contenido)).thenReturn(Optional.of(existente));
        when(calificacionRepository.findByContenido(contenido)).thenReturn(List.of(existente));

        CalificacionResponse response =
                calificacionService.calificarContenido(1L, "cliente@gmail.com", request);

        assertEquals(4, response.getPuntaje());
        assertEquals("Mejoró", response.getComentario());
        assertEquals(4.0, response.getPromedioCalificacion());
        verify(calificacionRepository).save(existente);
    }

    @Test
    void debeLanzarErrorSiUsuarioNoExiste() {
        CalificacionRequest request = new CalificacionRequest();
        request.setPuntaje(5);

        when(usuarioRepository.findByEmail("noexiste@gmail.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> calificacionService.calificarContenido(1L, "noexiste@gmail.com", request)
        );

        assertEquals("Usuario no encontrado", exception.getMessage());
    }
}