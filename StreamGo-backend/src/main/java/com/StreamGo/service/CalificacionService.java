package com.StreamGo.service;

import com.StreamGo.dto.request.CalificacionRequest;
import com.StreamGo.dto.response.CalificacionResponse;
import com.StreamGo.entity.CalificacionContenido;
import com.StreamGo.entity.Contenido;
import com.StreamGo.entity.Usuario;
import com.StreamGo.repository.CalificacionRepository;
import com.StreamGo.repository.ContenidoRepository;
import com.StreamGo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CalificacionService {

    private final CalificacionRepository calificacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final ContenidoRepository contenidoRepository;

    public CalificacionResponse calificarContenido(
            Long contenidoId,
            String email,
            CalificacionRequest request
    ) {

        log.debug("Iniciando proceso de calificación. Usuario: {}, Contenido ID: {}, Puntaje: {}", 
                email, contenidoId, request.getPuntaje());

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Usuario no encontrado con email: {}", email);
                    return new RuntimeException("Usuario no encontrado");
                });

        Contenido contenido = contenidoRepository.findById(contenidoId)
                .orElseThrow(() -> {
                    log.error("Contenido no encontrado con ID: {}", contenidoId);
                    return new RuntimeException("Contenido no encontrado");
                });

        log.debug("Usuario y contenido encontrados correctamente. Usuario: {}, Contenido: {}", 
                usuario.getEmail(), contenido.getTitulo());

        CalificacionContenido calificacion = calificacionRepository
                .findByUsuarioAndContenido(usuario, contenido)
                .orElse(null);

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

        calificacionRepository.save(calificacion);
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

    private void actualizarPromedio(Contenido contenido) {

        log.debug("Actualizando promedio de calificaciones para contenido: {}", contenido.getTitulo());

        List<CalificacionContenido> calificaciones =
                calificacionRepository.findByContenido(contenido);

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

        contenidoRepository.save(contenido);
        
        log.info("Promedio actualizado para '{}': {} estrellas ({} calificaciones)", 
                contenido.getTitulo(), promedio, totalCalificaciones);
    }
}