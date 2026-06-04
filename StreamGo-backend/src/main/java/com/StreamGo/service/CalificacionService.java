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

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Contenido contenido = contenidoRepository.findById(contenidoId)
                .orElseThrow(() -> new RuntimeException("Contenido no encontrado"));

        CalificacionContenido calificacion = calificacionRepository
                .findByUsuarioAndContenido(usuario, contenido)
                .orElse(null);

        if (calificacion == null) {

            calificacion = CalificacionContenido.builder()
                    .usuario(usuario)
                    .contenido(contenido)
                    .puntaje(request.getPuntaje())
                    .comentario(request.getComentario())
                    .fechaCalificacion(LocalDateTime.now())
                    .build();

        } else {

            calificacion.setPuntaje(request.getPuntaje());
            calificacion.setComentario(request.getComentario());
        }

        calificacionRepository.save(calificacion);

        actualizarPromedio(contenido);

        log.info(
                "Usuario {} calificó '{}' con {} estrellas",
                email,
                contenido.getTitulo(),
                request.getPuntaje()
        );

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

        List<CalificacionContenido> calificaciones =
                calificacionRepository.findByContenido(contenido);

        double promedio = calificaciones.stream()
                .mapToInt(CalificacionContenido::getPuntaje)
                .average()
                .orElse(0.0);

        contenido.setPromedioCalificacion(promedio);
        contenido.setTotalCalificaciones(calificaciones.size());

        contenidoRepository.save(contenido);
    }
}