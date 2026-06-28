package com.StreamGo.service;

import com.StreamGo.dto.request.NoticiaRequest;
import com.StreamGo.dto.response.NoticiaResponse;
import com.StreamGo.dao.NoticiaDAO;
import com.StreamGo.dao.UsuarioDAO;
import com.StreamGo.entity.Noticia;
import com.StreamGo.entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoticiaServiceTest {

    @Mock
    private NoticiaDAO noticiaDAO;

    @Mock
    private UsuarioDAO usuarioDAO;

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
        when(usuarioDAO.findById(1L)).thenReturn(mockAutor);
        when(usuarioDAO.findById(2L)).thenReturn(mockUsuario);
        doAnswer(invocation -> {
            Noticia noticia = invocation.getArgument(0);
            noticia.setIdPost(100L);
            noticia.setAutor(mockAutor);
            noticia.setUsuario(mockUsuario);
            return null;
        }).when(noticiaDAO).save(any(Noticia.class));

        // Act (Actuar)
        NoticiaResponse response = noticiaService.crearNoticia(mockRequest);

        // Assert (Afirmar)
        assertNotNull(response);
        assertEquals(100L, response.getIdPost());
        assertEquals("Nuevo Título", response.getTitulo());
        verify(noticiaDAO, times(1)).save(any(Noticia.class));
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
        verify(noticiaDAO, never()).save(any(Noticia.class));
    }

    @Test
    void crearNoticia_FallaPorAutorNoEncontrado() {
        // Arrange
        when(usuarioDAO.findById(1L)).thenThrow(new RuntimeException("Autor no encontrado"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            noticiaService.crearNoticia(mockRequest);
        });

        assertEquals("Autor no encontrado", exception.getMessage());
    }

    @Test
    void obtenerNoticia_Exito() {
        // Arrange
        when(noticiaDAO.findById(100L)).thenReturn(mockNoticia);

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
        when(noticiaDAO.findById(999L)).thenThrow(new RuntimeException("Noticia no encontrada"));

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
        when(noticiaDAO.findById(100L)).thenReturn(mockNoticia);

        // Act
        NoticiaResponse response = noticiaService.reaccionar(100L);

        // Assert
        assertEquals(reaccionesIniciales + 1, mockNoticia.getReacciones());
        assertEquals(reaccionesIniciales + 1, response.getReacciones());
        verify(noticiaDAO, times(1)).update(mockNoticia);
    }

    @Test
    void fijarNoticia_AlternaEstado() {
        // Arrange
        boolean estadoInicial = mockNoticia.isFijado(); // false
        when(noticiaDAO.findById(100L)).thenReturn(mockNoticia);

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
        when(noticiaDAO.findById(100L)).thenReturn(mockNoticia);

        // Act
        noticiaService.eliminarNoticia(100L);

        // Assert
        verify(noticiaDAO, times(1)).delete(100L);
    }
}
