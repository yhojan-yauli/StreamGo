package com.StreamGo.service;

import com.StreamGo.dto.request.CrearContenidoRequest;
import com.StreamGo.dto.response.ContenidoResponse;
import com.StreamGo.entity.Contenido;
import com.StreamGo.entity.Enum.EstadoContenido;
import com.StreamGo.entity.Enum.EstadoUsuario;
import com.StreamGo.entity.Enum.TipoContenido;
import com.StreamGo.entity.Usuario;
import com.StreamGo.repository.ContenidoRepository;
import com.StreamGo.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContenidoServiceTest {

    @Mock
    private ContenidoRepository contenidoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SuscripcionService suscripcionService;

    @InjectMocks
    private ContenidoService contenidoService;

    @Test
    void debeCrearContenido() {
        CrearContenidoRequest request = new CrearContenidoRequest();
        request.setTitulo("Naruto");
        request.setDescripcion("Anime ninja");
        request.setCategoria("Animes");
        request.setTipoContenido(TipoContenido.ANIME);
        request.setImagenUrl("imagen.jpg");
        request.setBannerUrl("banner.jpg");
        request.setVideoUrl("video.mp4");
        request.setFechaEstreno(LocalDate.now());
        request.setDuracionMinutos(24);
        request.setGratuito(true);
        request.setRecomendado(true);
        request.setTendencia(false);
        request.setEstado(EstadoContenido.SINLOGIN);

        Contenido guardado = Contenido.builder()
                .id(1L)
                .titulo("Naruto")
                .descripcion("Anime ninja")
                .categoria("Animes")
                .tipoContenido(TipoContenido.ANIME)
                .imagenUrl("imagen.jpg")
                .bannerUrl("banner.jpg")
                .videoUrl("video.mp4")
                .fechaEstreno(request.getFechaEstreno())
                .duracionMinutos(24)
                .gratuito(true)
                .recomendado(true)
                .tendencia(false)
                .estado(EstadoContenido.SINLOGIN)
                .promedioCalificacion(0.0)
                .totalCalificaciones(0)
                .totalReproducciones(0)
                .build();

        when(contenidoRepository.save(any(Contenido.class))).thenReturn(guardado);

        ContenidoResponse response = contenidoService.crearContenido(request);

        assertEquals("Naruto", response.getTitulo());
        assertEquals(EstadoContenido.SINLOGIN, response.getEstado());
        verify(contenidoRepository).save(any(Contenido.class));
    }

    @Test
    void debeListarSoloContenidoSinLogin() {
        Contenido publico = Contenido.builder()
                .id(1L)
                .titulo("Publico")
                .estado(EstadoContenido.SINLOGIN)
                .build();

        when(contenidoRepository.findByEstado(EstadoContenido.SINLOGIN))
                .thenReturn(List.of(publico));

        List<ContenidoResponse> response = contenidoService.listarSinLogin();

        assertEquals(1, response.size());
        assertEquals("Publico", response.get(0).getTitulo());
    }

    @Test
    void usuarioInactivoDebeVerInactivoYSinLogin() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .email("cliente@gmail.com")
                .estado(EstadoUsuario.INACTIVO)
                .build();

        Contenido sinLogin = Contenido.builder()
                .id(1L)
                .titulo("Gratis")
                .estado(EstadoContenido.SINLOGIN)
                .build();

        Contenido inactivo = Contenido.builder()
                .id(2L)
                .titulo("Limitado")
                .estado(EstadoContenido.INACTIVO)
                .build();

        Contenido activo = Contenido.builder()
                .id(3L)
                .titulo("Premium")
                .estado(EstadoContenido.ACTIVO)
                .build();

        when(usuarioRepository.findByEmail("cliente@gmail.com")).thenReturn(Optional.of(usuario));
        when(suscripcionService.usuarioTieneSuscripcionActiva(usuario)).thenReturn(false);
        when(contenidoRepository.findAll()).thenReturn(List.of(sinLogin, inactivo, activo));

        List<ContenidoResponse> response = contenidoService.listarParaUsuario("cliente@gmail.com");

        assertEquals(2, response.size());
        assertTrue(response.stream().noneMatch(c -> c.getEstado() == EstadoContenido.ACTIVO));
    }

    @Test
    void usuarioActivoDebeVerTodo() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .email("cliente@gmail.com")
                .estado(EstadoUsuario.ACTIVO)
                .build();

        Contenido activo = Contenido.builder()
                .id(1L)
                .titulo("Premium")
                .estado(EstadoContenido.ACTIVO)
                .build();

        when(usuarioRepository.findByEmail("cliente@gmail.com")).thenReturn(Optional.of(usuario));
        when(contenidoRepository.findAll()).thenReturn(List.of(activo));

        List<ContenidoResponse> response = contenidoService.listarParaUsuario("cliente@gmail.com");

        assertEquals(1, response.size());
        assertEquals(EstadoContenido.ACTIVO, response.get(0).getEstado());
    }
}