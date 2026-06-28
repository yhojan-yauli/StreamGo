package com.StreamGo.service;

import com.StreamGo.dto.request.ContenidoVotableRequest;
import com.StreamGo.dto.request.PeticionRequest;
import com.StreamGo.dto.response.ContenidoVotableResponse;
import com.StreamGo.dto.response.PeticionResponse;
import com.StreamGo.dto.response.VotoResponse;
import com.StreamGo.dao.ContenidoVotableDAO;
import com.StreamGo.dao.PeticionDAO;
import com.StreamGo.dao.UsuarioDAO;
import com.StreamGo.entity.ContenidoVotable;
import com.StreamGo.entity.Peticion;
import com.StreamGo.entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del Servicio de Peticiones")
class PeticionServiceTest {

    @Mock
    private PeticionDAO peticionDAO;

    @Mock
    private ContenidoVotableDAO contenidoVotableDAO;

    @Mock
    private UsuarioDAO usuarioDAO;

    @InjectMocks
    private PeticionService peticionService;

    private ContenidoVotable contenidoVotable;
    private ContenidoVotableRequest contenidoVotableRequest;
    private Usuario usuario;
    private Peticion peticion;
    private PeticionRequest peticionRequest;

    @BeforeEach
    void setUp() {
        contenidoVotable = ContenidoVotable.builder()
                .id(1L)
                .titulo("Película de prueba")
                .descripcion("Descripción de prueba")
                .posterUrl("poster.jpg")
                .imagenUrl("imagen.jpg")
                .activo(true)
                .build();

        contenidoVotableRequest = new ContenidoVotableRequest();
        contenidoVotableRequest.setTitulo("Película de prueba");
        contenidoVotableRequest.setDescripcion("Descripción de prueba");
        contenidoVotableRequest.setPosterUrl("poster.jpg");
        contenidoVotableRequest.setImagenUrl("imagen.jpg");

        usuario = Usuario.builder()
                .id(1L)
                .email("test@test.com")
                .nombre("Usuario Test")
                .build();

        peticion = Peticion.builder()
                .id(1L)
                .usuario(usuario)
                .contenidoVotable(contenidoVotable)
                .fechaPeticion(LocalDateTime.now())
                .build();

        peticionRequest = new PeticionRequest();
        peticionRequest.setContenidoVotableId(1L);
    }

    // ============================================================
    // 1. ADMIN - Agregar contenido votable
    // ============================================================
    @Test
    @DisplayName("Admin: Agregar contenido votable")
    void testAgregarVotable() {
        doAnswer(invocation -> {
            ContenidoVotable votable = invocation.getArgument(0);
            votable.setId(1L);
            return null;
        }).when(contenidoVotableDAO).save(any(ContenidoVotable.class));

        ContenidoVotableResponse response = peticionService.agregarVotable(contenidoVotableRequest);

        assertNotNull(response);
        assertEquals("Película de prueba", response.getTitulo());
        verify(contenidoVotableDAO, times(1)).save(any(ContenidoVotable.class));
    }

    // ============================================================
    // 2. ADMIN - Editar contenido votable
    // ============================================================
    @Test
    @DisplayName("Admin: Editar contenido votable")
    void testEditarVotable() {
        ContenidoVotableRequest requestEditado = new ContenidoVotableRequest();
        requestEditado.setTitulo("Película editada");
        requestEditado.setDescripcion("Nueva descripción");
        requestEditado.setPosterUrl("nuevo-poster.jpg");
        requestEditado.setImagenUrl("nueva-imagen.jpg");

        when(contenidoVotableDAO.findById(1L))
                .thenReturn(contenidoVotable);

        ContenidoVotableResponse response = peticionService.editarVotable(1L, requestEditado);

        assertNotNull(response);
        verify(contenidoVotableDAO, times(1)).findById(1L);
        verify(contenidoVotableDAO, times(1)).update(any(ContenidoVotable.class));
    }

    // ============================================================
    // 3. ADMIN - Desactivar contenido votable
    // ============================================================
    @Test
    @DisplayName("Admin: Desactivar contenido votable")
    void testDesactivarVotable() {
        when(contenidoVotableDAO.findById(1L))
                .thenReturn(contenidoVotable);
        when(peticionDAO.countByContenidoVotableId(1L))
                .thenReturn(5L);

        peticionService.desactivarVotable(1L);

        assertFalse(contenidoVotable.getActivo());
        verify(contenidoVotableDAO, times(1)).update(contenidoVotable);
    }

    // ============================================================
    // 4. ADMIN - Ver ranking de votos
    // ============================================================
    @Test
    @DisplayName("Admin: Ver ranking de votos")
    void testVerRankingVotos() {
        // Crear la lista de Object[] correctamente
        List<Object[]> mockRanking = new ArrayList<>();
        mockRanking.add(new Object[]{1L, "Película de prueba", 10L});

        when(peticionDAO.contarVotosPorContenido())
                .thenReturn(mockRanking);

        List<VotoResponse> ranking = peticionService.verRankingVotos();

        assertNotNull(ranking);
        assertEquals(1, ranking.size());
        assertEquals(10L, ranking.get(0).getTotalVotos());
        verify(peticionDAO, times(1)).contarVotosPorContenido();
    }

    // ============================================================
    // 5. CLIENTE - Listar contenidos votables activos
    // ============================================================
    @Test
    @DisplayName("Cliente: Listar contenidos votables activos")
    void testListarVotables() {
        List<ContenidoVotable> contenidos = Arrays.asList(contenidoVotable);
        when(contenidoVotableDAO.findByActivoTrue())
                .thenReturn(contenidos);

        List<ContenidoVotableResponse> lista = peticionService.listarVotables();

        assertNotNull(lista);
        assertEquals(1, lista.size());
        verify(contenidoVotableDAO, times(1)).findByActivoTrue();
    }

    // ============================================================
    // 6. CLIENTE - Elegir película (votar)
    // ============================================================
    @Test
    @DisplayName("Cliente: Elegir película (votar)")
    void testElegirPelicula() {
        when(usuarioDAO.findByEmail("test@test.com"))
                .thenReturn(Optional.of(usuario));
        when(contenidoVotableDAO.findById(1L))
                .thenReturn(contenidoVotable);
        when(peticionDAO.findByUsuarioId(1L))
                .thenReturn(Optional.empty());
        doAnswer(invocation -> {
            Peticion nuevaPeticion = invocation.getArgument(0);
            nuevaPeticion.setId(1L);
            return null;
        }).when(peticionDAO).save(any(Peticion.class));

        PeticionResponse response = peticionService.elegirPelicula("test@test.com", peticionRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(peticionDAO, times(1)).save(any(Peticion.class));
    }
}
