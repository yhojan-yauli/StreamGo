package com.StreamGo.service;

import com.StreamGo.dto.response.ReproduccionResponse;
import com.StreamGo.entity.Contenido;
import com.StreamGo.entity.Enum.EstadoContenido;
import com.StreamGo.entity.Enum.EstadoUsuario;
import com.StreamGo.entity.Usuario;
import com.StreamGo.repository.ContenidoRepository;
import com.StreamGo.repository.HistorialReproduccionRepository;
import com.StreamGo.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReproduccionServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ContenidoRepository contenidoRepository;

    @Mock
    private SuscripcionService suscripcionService;

    @Mock
    private HistorialReproduccionRepository historialRepository;

    @InjectMocks
    private ReproduccionService reproduccionService;

    @Test
    void debeReproducirContenidoSinLoginParaUsuarioLogueado() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .email("cliente@gmail.com")
                .estado(EstadoUsuario.INACTIVO)
                .build();

        Contenido contenido = Contenido.builder()
                .id(1L)
                .titulo("Naruto")
                .estado(EstadoContenido.SINLOGIN)
                .totalReproducciones(0)
                .videoUrl("video.mp4")
                .build();

        when(usuarioRepository.findByEmail("cliente@gmail.com")).thenReturn(Optional.of(usuario));
        when(contenidoRepository.findById(1L)).thenReturn(Optional.of(contenido));

        ReproduccionResponse response = reproduccionService.reproducir(1L, "cliente@gmail.com");

        assertEquals("Naruto", response.getTitulo());
        assertEquals("Reproducción iniciada", response.getMensaje());
        verify(historialRepository).save(any());
        verify(contenidoRepository).save(contenido);
    }

    @Test
    void debeBloquearUsuarioSuspendido() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .email("cliente@gmail.com")
                .estado(EstadoUsuario.SUSPENDIDO)
                .build();

        Contenido contenido = Contenido.builder()
                .id(1L)
                .titulo("Naruto")
                .estado(EstadoContenido.SINLOGIN)
                .build();

        when(usuarioRepository.findByEmail("cliente@gmail.com")).thenReturn(Optional.of(usuario));
        when(contenidoRepository.findById(1L)).thenReturn(Optional.of(contenido));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reproduccionService.reproducir(1L, "cliente@gmail.com")
        );

        assertEquals("Tu cuenta se encuentra suspendida", exception.getMessage());
        verify(historialRepository, never()).save(any());
    }

    @Test
    void debePermitirReproduccionPublicaSoloSinLogin() {
        Contenido contenido = Contenido.builder()
                .id(1L)
                .titulo("Contenido Público")
                .estado(EstadoContenido.SINLOGIN)
                .totalReproducciones(0)
                .videoUrl("video.mp4")
                .build();

        when(contenidoRepository.findById(1L)).thenReturn(Optional.of(contenido));

        ReproduccionResponse response = reproduccionService.reproducirPublico(1L);

        assertEquals("Contenido Público", response.getTitulo());
        assertEquals("Reproducción pública iniciada", response.getMensaje());
    }

    @Test
    void debeBloquearReproduccionPublicaDeContenidoActivo() {
        Contenido contenido = Contenido.builder()
                .id(1L)
                .titulo("Premium")
                .estado(EstadoContenido.ACTIVO)
                .build();

        when(contenidoRepository.findById(1L)).thenReturn(Optional.of(contenido));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reproduccionService.reproducirPublico(1L)
        );

        
        assertEquals("Este contenido requiere iniciar sesión", exception.getMessage());
    }
}