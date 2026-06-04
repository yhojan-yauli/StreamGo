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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PeticionService {

    private static final Logger log = LoggerFactory.getLogger(PeticionService.class);

    private final PeticionRepository peticionRepository;
    private final ContenidoVotableRepository contenidoVotableRepository;
    private final UsuarioRepository usuarioRepository;

    // ── ADMIN ──────────────────────────────────────────

    public ContenidoVotableResponse agregarVotable(ContenidoVotableRequest request) {
        log.debug("Agregando nuevo contenido votable: titulo={}", request.getTitulo());
        ContenidoVotable votable = ContenidoVotable.builder()
                .titulo(request.getTitulo())
                .descripcion(request.getDescripcion())
                .posterUrl(request.getPosterUrl())
                .imagenUrl(request.getImagenUrl())
                .activo(true)
                .build();
        ContenidoVotableResponse response = mapVotableToResponse(contenidoVotableRepository.save(votable));
        log.info("Contenido votable creado exitosamente: id={}, titulo={}", response.getId(), response.getTitulo());
        return response;
    }

    public ContenidoVotableResponse editarVotable(Long id, ContenidoVotableRequest request) {
        log.debug("Editando contenido votable: id={}", id);
        ContenidoVotable votable = contenidoVotableRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Contenido votable no encontrado para editar: id={}", id);
                    return new RuntimeException("Contenido votable no encontrado");
                });

        if (!votable.getActivo()) {
            log.warn("Se está editando un contenido votable inactivo: id={}, titulo={}", id, votable.getTitulo());
        }

        votable.setTitulo(request.getTitulo());
        votable.setDescripcion(request.getDescripcion());
        votable.setPosterUrl(request.getPosterUrl());
        votable.setImagenUrl(request.getImagenUrl());
        ContenidoVotableResponse response = mapVotableToResponse(contenidoVotableRepository.save(votable));
        log.info("Contenido votable editado exitosamente: id={}, titulo={}", response.getId(), response.getTitulo());
        return response;
    }

    public void desactivarVotable(Long id) {
        log.debug("Desactivando contenido votable: id={}", id);
        ContenidoVotable votable = contenidoVotableRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Contenido votable no encontrado para desactivar: id={}", id);
                    return new RuntimeException("Contenido votable no encontrado");
                });

        long votos = peticionRepository.countByContenidoVotableId(id);
        if (votos > 0) {
            log.warn("Se está desactivando un contenido votable con {} votos: id={}, titulo={}", votos, id, votable.getTitulo());
        }

        votable.setActivo(false);
        contenidoVotableRepository.save(votable);
        log.info("Contenido votable desactivado: id={}, titulo={}", id, votable.getTitulo());
    }

    public List<VotoResponse> verRankingVotos() {
        log.debug("Consultando ranking de votos");
        List<VotoResponse> ranking = peticionRepository.contarVotosPorContenido()
                .stream()
                .map(row -> VotoResponse.builder()
                        .contenidoVotableId((Long) row[0])
                        .titulo((String) row[1])
                        .totalVotos((Long) row[2])
                        .build())
                .toList();

        if (ranking.isEmpty()) {
            log.warn("No hay votos registrados aún");
        } else {
            log.info("Ranking obtenido: {} entradas", ranking.size());
        }

        return ranking;
    }

    // ── CLIENTE ────────────────────────────────────────

    public List<ContenidoVotableResponse> listarVotables() {
        log.debug("Listando contenidos votables activos");
        List<ContenidoVotableResponse> lista = contenidoVotableRepository.findByActivoTrue()
                .stream()
                .map(this::mapVotableToResponse)
                .toList();

        if (lista.isEmpty()) {
            log.warn("No hay contenidos votables activos disponibles");
        } else {
            log.info("Contenidos votables activos encontrados: {}", lista.size());
        }

        return lista;
    }

    public PeticionResponse elegirPelicula(String email, PeticionRequest request) {
        log.debug("Usuario {} intentando elegir contenido votable id={}", email, request.getContenidoVotableId());

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Usuario no encontrado: email={}", email);
                    return new RuntimeException("Usuario no encontrado");
                });

        ContenidoVotable votable = contenidoVotableRepository.findById(request.getContenidoVotableId())
                .orElseThrow(() -> {
                    log.error("Contenido votable no encontrado: id={}", request.getContenidoVotableId());
                    return new RuntimeException("Contenido votable no encontrado");
                });

        if (!votable.getActivo()) {
            log.warn("Usuario {} intentó votar por un contenido votable inactivo: id={}, titulo={}",
                    email, votable.getId(), votable.getTitulo());
        }

        Optional<Peticion> existente = peticionRepository.findByUsuarioId(usuario.getId());

        Peticion peticion;
        if (existente.isPresent()) {
            log.warn("Usuario {} está cambiando su elección: titulo anterior={} → nuevo titulo={}",
                    email, existente.get().getContenidoVotable().getTitulo(), votable.getTitulo());
            peticion = existente.get();
            peticion.setContenidoVotable(votable);
            peticion.setFechaPeticion(LocalDateTime.now());
        } else {
            log.debug("Creando nueva petición para usuario {}", email);
            peticion = Peticion.builder()
                    .usuario(usuario)
                    .contenidoVotable(votable)
                    .fechaPeticion(LocalDateTime.now())
                    .build();
        }

        PeticionResponse response = mapPeticionToResponse(peticionRepository.save(peticion));
        log.info("Petición guardada: usuarioId={}, contenidoVotableId={}, titulo={}",
                usuario.getId(), votable.getId(), votable.getTitulo());
        return response;
    }

    // ── MAPPERS ────────────────────────────────────────

    private ContenidoVotableResponse mapVotableToResponse(ContenidoVotable v) {
        return ContenidoVotableResponse.builder()
                .id(v.getId())
                .titulo(v.getTitulo())
                .descripcion(v.getDescripcion())
                .posterUrl(v.getPosterUrl())
                .imagenUrl(v.getImagenUrl())
                .activo(v.getActivo())
                .build();
    }

    private PeticionResponse mapPeticionToResponse(Peticion p) {
        return PeticionResponse.builder()
                .id(p.getId())
                .usuarioId(p.getUsuario().getId())
                .contenidoVotableId(p.getContenidoVotable().getId())
                .tituloPelicula(p.getContenidoVotable().getTitulo())
                .fechaPeticion(p.getFechaPeticion())
                .build();
    }
}