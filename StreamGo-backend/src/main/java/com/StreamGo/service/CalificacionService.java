package com.StreamGo.service;

import com.StreamGo.dto.request.CalificacionRequest;
import com.StreamGo.dto.response.CalificacionResponse;
import com.StreamGo.dao.CalificacionDAO;
import com.StreamGo.dao.ContenidoDAO;
import com.StreamGo.dao.UsuarioDAO;
import com.StreamGo.entity.CalificacionContenido;
import com.StreamGo.entity.Contenido;
import com.StreamGo.entity.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
/**
 * Servicio encargado de la lógica de calificaciones.
 *
 * Permite registrar, actualizar y recalcular el promedio
 * de calificación de un contenido.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CalificacionService {

    private final CalificacionDAO calificacionDAO;
    private final UsuarioDAO usuarioDAO;
    private final ContenidoDAO contenidoDAO;

    public CalificacionResponse calificarContenido(
            Long contenidoId,
            String email,
            CalificacionRequest request
    ) {

        log.debug("Iniciando proceso de calificación. Usuario: {}, Contenido ID: {}, Puntaje: {}", 
                email, contenidoId, request.getPuntaje());

        Usuario usuario = usuarioDAO.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Usuario no encontrado con email: {}", email);
                    return new RuntimeException("Usuario no encontrado");
                });

        Contenido contenido = contenidoDAO.findById(contenidoId);

        log.debug("Usuario y contenido encontrados correctamente. Usuario: {}, Contenido: {}", 
                usuario.getEmail(), contenido.getTitulo());

        CalificacionContenido calificacion = calificacionDAO
                .findByUsuarioAndContenido(usuario, contenido)
                .orElse(null);

        boolean nuevaCalificacion = calificacion == null;

        if (calificacion == null) {

            log.debug("Creando nueva calificación para el usuario {} en el contenido {}", 
                    email, contenido.getTitulo());

            calificacion = CalificacionContenido.builder()
                    .usuario(usuario)
                    .contenido(contenido)
                    .puntaje(request.getPuntaje())
                    .comentario(request.getComentario())
                    .fechaCalificacion(LocalDateTime.now())
                    .build();

        } else {

            log.debug("Actualizando calificación existente. Puntaje anterior: {}, Nuevo puntaje: {}", 
                    calificacion.getPuntaje(), request.getPuntaje());

            calificacion.setPuntaje(request.getPuntaje());
            calificacion.setComentario(request.getComentario());
        }

        if (nuevaCalificacion) {
            calificacionDAO.save(calificacion);
        } else {
            calificacionDAO.update(calificacion);
        }
        log.debug("Calificación guardada en base de datos");

        actualizarPromedio(contenido);

        log.info("Usuario {} calificó '{}' con {} estrellas",
                email,
                contenido.getTitulo(),
                request.getPuntaje()
        );

        if (request.getComentario() != null && !request.getComentario().isEmpty()) {
            log.debug("Usuario {} dejó comentario en '{}': {}", 
                    email, contenido.getTitulo(), request.getComentario());
        }

        return CalificacionResponse.builder()
                .contenidoId(contenido.getId())
                .titulo(contenido.getTitulo())
                .puntaje(request.getPuntaje())
                .comentario(request.getComentario())
                .promedioCalificacion(contenido.getPromedioCalificacion())
                .totalCalificaciones(contenido.getTotalCalificaciones())
                .mensaje("Calificación registrada correctamente")
                .build();
    }
/**
 * Recalcula el promedio de calificación de un contenido.
 *
 * @param contenido contenido al que se le actualiza el promedio.
 */
    private void actualizarPromedio(Contenido contenido) {

        log.debug("Actualizando promedio de calificaciones para contenido: {}", contenido.getTitulo());

        List<CalificacionContenido> calificaciones =
                calificacionDAO.findByContenido(contenido);

        int totalCalificaciones = calificaciones.size();
        double promedio = calificaciones.stream()
                .mapToInt(CalificacionContenido::getPuntaje)
                .average()
                .orElse(0.0);

        log.debug("Contenido '{}' - Total calificaciones: {}, Promedio calculado: {}. Anterior promedio: {}, Anterior total: {}", 
                contenido.getTitulo(), 
                totalCalificaciones, 
                promedio, 
                contenido.getPromedioCalificacion(), 
                contenido.getTotalCalificaciones());

        contenido.setPromedioCalificacion(promedio);
        contenido.setTotalCalificaciones(totalCalificaciones);

        contenidoDAO.update(contenido);
        
        log.info("Promedio actualizado para '{}': {} estrellas ({} calificaciones)", 
                contenido.getTitulo(), promedio, totalCalificaciones);
    }
}
