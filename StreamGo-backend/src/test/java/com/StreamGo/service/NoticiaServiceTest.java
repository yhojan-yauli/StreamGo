package com.StreamGo.service;

import com.StreamGo.dto.request.NoticiaRequest;
import com.StreamGo.dto.response.NoticiaResponse;
import com.StreamGo.entity.Noticia;
import com.StreamGo.entity.Usuario;
import com.StreamGo.repository.NoticiaRepository;
import com.StreamGo.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoticiaServiceTest {

    @Mock
    private NoticiaRepository noticiaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private NoticiaService noticiaService;

    private Usuario mockAutor;
    private Usuario mockUsuario;
    private Noticia mockNoticia;
    private NoticiaRequest mockRequest;

    @BeforeEach
    void setUp() {
        // Preparar datos de prueba reutilizables
        mockAutor = new Usuario();
        mockAutor.setId(1L);
        mockAutor.setNombre("Autor Test");

        mockUsuario = new Usuario();
        mockUsuario.setId(2L);
        mockUsuario.setNombre("Usuario Test");

        mockNoticia = Noticia.builder()
                .idPost(100L)
                .autor(mockAutor)
                .usuario(mockUsuario)
                .titulo("Título de Prueba")
                .contenido("Contenido de prueba")
                .reacciones(5)
                .fijado(false)
                .build();

        mockRequest = new NoticiaRequest();
        mockRequest.setIdAutor(1L);
        mockRequest.setIdUsuario(2L);
        mockRequest.setTitulo("Nuevo Título");
        mockRequest.setContenido("Nuevo Contenido");
        mockRequest.setReacciones(0);
    }

    @Test
    void crearNoticia_Exito() {
        // Arrange (Preparar)
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(mockAutor));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(mockUsuario));
        when(noticiaRepository.save(any(Noticia.class))).thenReturn(mockNoticia);

        // Act (Actuar)
        NoticiaResponse response = noticiaService.crearNoticia(mockRequest);

        // Assert (Afirmar)
        assertNotNull(response);
        assertEquals(100L, response.getIdPost());
        assertEquals("Título de Prueba", response.getTitulo());
        verify(noticiaRepository, times(1)).save(any(Noticia.class));
    }

    @Test
    void crearNoticia_FallaPorTituloVacio() {
        // Arrange
        mockRequest.setTitulo("   "); // Título inválido

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            noticiaService.crearNoticia(mockRequest);
        });

        assertEquals("El título es obligatorio", exception.getMessage());
        verify(noticiaRepository, never()).save(any(Noticia.class));
    }

    @Test
    void crearNoticia_FallaPorAutorNoEncontrado() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            noticiaService.crearNoticia(mockRequest);
        });

        assertEquals("Autor no encontrado", exception.getMessage());
    }

    @Test
    void obtenerNoticia_Exito() {
        // Arrange
        when(noticiaRepository.findById(100L)).thenReturn(Optional.of(mockNoticia));

        // Act
        NoticiaResponse response = noticiaService.obtenerNoticia(100L);

        // Assert
        assertNotNull(response);
        assertEquals(100L, response.getIdPost());
        assertEquals("Autor Test", response.getAutorNombre());
    }

    @Test
    void obtenerNoticia_FallaPorNoEncontrada() {
        // Arrange
        when(noticiaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            noticiaService.obtenerNoticia(999L);
        });

        assertEquals("Noticia no encontrada", exception.getMessage());
    }

    @Test
    void reaccionar_IncrementaReacciones() {
        // Arrange
        int reaccionesIniciales = mockNoticia.getReacciones();
        when(noticiaRepository.findById(100L)).thenReturn(Optional.of(mockNoticia));
        when(noticiaRepository.save(any(Noticia.class))).thenReturn(mockNoticia);

        // Act
        NoticiaResponse response = noticiaService.reaccionar(100L);

        // Assert
        assertEquals(reaccionesIniciales + 1, mockNoticia.getReacciones());
        assertEquals(reaccionesIniciales + 1, response.getReacciones());
        verify(noticiaRepository, times(1)).save(mockNoticia);
    }

    @Test
    void fijarNoticia_AlternaEstado() {
        // Arrange
        boolean estadoInicial = mockNoticia.isFijado(); // false
        when(noticiaRepository.findById(100L)).thenReturn(Optional.of(mockNoticia));
        when(noticiaRepository.save(any(Noticia.class))).thenReturn(mockNoticia);

        // Act
        NoticiaResponse response = noticiaService.fijarNoticia(100L);

        // Assert
        assertTrue(mockNoticia.isFijado()); // Ahora debe ser true
        assertTrue(response.isFijado());
        assertNotEquals(estadoInicial, mockNoticia.isFijado());
    }

    @Test
    void eliminarNoticia_Exito() {
        // Arrange
        when(noticiaRepository.findById(100L)).thenReturn(Optional.of(mockNoticia));

        // Act
        noticiaService.eliminarNoticia(100L);

        // Assert
        verify(noticiaRepository, times(1)).delete(mockNoticia);
    }
}