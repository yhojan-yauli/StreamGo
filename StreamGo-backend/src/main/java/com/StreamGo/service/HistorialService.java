package com.StreamGo.service;

import com.StreamGo.dto.response.HistorialResponse;
import com.StreamGo.dao.HistorialReproduccionDAO;
import com.StreamGo.dao.UsuarioDAO;
import com.StreamGo.entity.HistorialReproduccion;
import com.StreamGo.entity.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * Servicio encargado de obtener el historial de reproducción.
 *
 * Consulta las reproducciones registradas por usuario
 * y las transforma en respuestas para el cliente.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class HistorialService {

    private final HistorialReproduccionDAO historialDAO;
    private final UsuarioDAO usuarioDAO;

    /**
     * Obtiene el historial de reproducción para un usuario específico.
     *
     * @param email correo del usuario.
     * @return lista de reproducciones del usuario.
     */
    public List<HistorialResponse> obtenerHistorial(String email) {

        log.debug("Intentando obtener historial para el usuario: {}", email);

        Usuario usuario = usuarioDAO.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Usuario no encontrado con email: {}", email);
                    return new RuntimeException("Usuario no encontrado");
                });

        log.info("Consultando historial de reproducción del usuario {}", email);

        List<HistorialResponse> historial = historialDAO.findByUsuarioOrderByFechaReproduccionDesc(usuario)
                .stream()
                .map(this::mapToResponse)
                .toList();

        log.debug("Historial recuperado para usuario {}. Total de registros: {}", email, historial.size());

        if (historial.isEmpty()) {
            log.info("El usuario {} no tiene historial de reproducción", email);
        }

        return historial;
    }
/**
 * Convierte una entidad HistorialReproduccion en HistorialResponse.
 *
 * @param historial entidad de historial.
 * @return respuesta con información del historial.
 */
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
