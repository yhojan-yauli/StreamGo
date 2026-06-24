package com.StreamGo.service;

import com.StreamGo.dto.request.ContenidoVotableRequest;
import com.StreamGo.dto.request.PeticionRequest;
import com.StreamGo.dto.response.ContenidoVotableResponse;
import com.StreamGo.dto.response.PeticionResponse;
import com.StreamGo.dto.response.VotoResponse;
import com.StreamGo.entity.ContenidoVotable;
import com.StreamGo.entity.Peticion;
import com.StreamGo.entity.Usuario;
import com.StreamGo.repository.ContenidoVotableRepository;
import com.StreamGo.repository.PeticionRepository;
import com.StreamGo.repository.UsuarioRepository;
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
    private PeticionRepository peticionRepository;

    @Mock
    private ContenidoVotableRepository contenidoVotableRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

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
        when(contenidoVotableRepository.save(any(ContenidoVotable.class)))
                .thenReturn(contenidoVotable);

        ContenidoVotableResponse response = peticionService.agregarVotable(contenidoVotableRequest);

        assertNotNull(response);
        assertEquals("Película de prueba", response.getTitulo());
        verify(contenidoVotableRepository, times(1)).save(any(ContenidoVotable.class));
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

        when(contenidoVotableRepository.findById(1L))
                .thenReturn(Optional.of(contenidoVotable));
        when(contenidoVotableRepository.save(any(ContenidoVotable.class)))
                .thenReturn(contenidoVotable);

        ContenidoVotableResponse response = peticionService.editarVotable(1L, requestEditado);

        assertNotNull(response);
        verify(contenidoVotableRepository, times(1)).findById(1L);
        verify(contenidoVotableRepository, times(1)).save(any(ContenidoVotable.class));
    }

    // ============================================================
    // 3. ADMIN - Desactivar contenido votable
    // ============================================================
    @Test
    @DisplayName("Admin: Desactivar contenido votable")
    void testDesactivarVotable() {
        when(contenidoVotableRepository.findById(1L))
                .thenReturn(Optional.of(contenidoVotable));
        when(peticionRepository.countByContenidoVotableId(1L))
                .thenReturn(5L);

        peticionService.desactivarVotable(1L);

        assertFalse(contenidoVotable.getActivo());
        verify(contenidoVotableRepository, times(1)).save(contenidoVotable);
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

        when(peticionRepository.contarVotosPorContenido())
                .thenReturn(mockRanking);

        List<VotoResponse> ranking = peticionService.verRankingVotos();

        assertNotNull(ranking);
        assertEquals(1, ranking.size());
        assertEquals(10L, ranking.get(0).getTotalVotos());
        verify(peticionRepository, times(1)).contarVotosPorContenido();
    }

    // ============================================================
    // 5. CLIENTE - Listar contenidos votables activos
    // ============================================================
    @Test
    @DisplayName("Cliente: Listar contenidos votables activos")
    void testListarVotables() {
        List<ContenidoVotable> contenidos = Arrays.asList(contenidoVotable);
        when(contenidoVotableRepository.findByActivoTrue())
                .thenReturn(contenidos);

        List<ContenidoVotableResponse> lista = peticionService.listarVotables();

        assertNotNull(lista);
        assertEquals(1, lista.size());
        verify(contenidoVotableRepository, times(1)).findByActivoTrue();
    }

    // ============================================================
    // 6. CLIENTE - Elegir película (votar)
    // ============================================================
    @Test
    @DisplayName("Cliente: Elegir película (votar)")
    void testElegirPelicula() {
        when(usuarioRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(usuario));
        when(contenidoVotableRepository.findById(1L))
                .thenReturn(Optional.of(contenidoVotable));
        when(peticionRepository.findByUsuarioId(1L))
                .thenReturn(Optional.empty());
        when(peticionRepository.save(any(Peticion.class)))
                .thenReturn(peticion);

        PeticionResponse response = peticionService.elegirPelicula("test@test.com", peticionRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(peticionRepository, times(1)).save(any(Peticion.class));
    }
}