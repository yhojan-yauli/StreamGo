package com.StreamGo.service;

import com.StreamGo.dto.response.HistorialResponse;
import com.StreamGo.entity.HistorialReproduccion;
import com.StreamGo.entity.Usuario;
import com.StreamGo.repository.HistorialReproduccionRepository;
import com.StreamGo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HistorialService {

    private final HistorialReproduccionRepository historialRepository;
    private final UsuarioRepository usuarioRepository;

    public List<HistorialResponse> obtenerHistorial(String email) {

        log.debug("Intentando obtener historial para el usuario: {}", email);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Usuario no encontrado con email: {}", email);
                    return new RuntimeException("Usuario no encontrado");
                });

        log.info("Consultando historial de reproducción del usuario {}", email);

        List<HistorialResponse> historial = historialRepository.findByUsuarioOrderByFechaReproduccionDesc(usuario)
                .stream()
                .map(this::mapToResponse)
                .toList();

        log.debug("Historial recuperado para usuario {}. Total de registros: {}", email, historial.size());

        if (historial.isEmpty()) {
            log.info("El usuario {} no tiene historial de reproducción", email);
        }

        return historial;
    }

    private HistorialResponse mapToResponse(HistorialReproduccion historial) {

        log.debug("Mapeando historial ID: {} para contenido: {}", 
                historial.getId(), 
                historial.getContenido().getTitulo());

        return HistorialResponse.builder()
                .historialId(historial.getId())
                .contenidoId(historial.getContenido().getId())
                .titulo(historial.getContenido().getTitulo())
                .imagenUrl(historial.getContenido().getImagenUrl())
                .categoria(historial.getContenido().getCategoria())
                .fechaReproduccion(historial.getFechaReproduccion())
                .progresoSegundos(historial.getProgresoSegundos())
                .completado(historial.getCompletado())
                .build();
    }
}