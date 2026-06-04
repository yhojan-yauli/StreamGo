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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PeticionService {

    private final PeticionRepository peticionRepository;
    private final ContenidoVotableRepository contenidoVotableRepository;
    private final UsuarioRepository usuarioRepository;

    // ── ADMIN ──────────────────────────────────────────

    public ContenidoVotableResponse agregarVotable(ContenidoVotableRequest request) {
        ContenidoVotable votable = ContenidoVotable.builder()
                .titulo(request.getTitulo())
                .descripcion(request.getDescripcion())
                .posterUrl(request.getPosterUrl())
                .imagenUrl(request.getImagenUrl())
                .activo(true)
                .build();
        return mapVotableToResponse(contenidoVotableRepository.save(votable));
    }

    public ContenidoVotableResponse editarVotable(Long id, ContenidoVotableRequest request) {
        ContenidoVotable votable = contenidoVotableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contenido votable no encontrado"));
        votable.setTitulo(request.getTitulo());
        votable.setDescripcion(request.getDescripcion());
        votable.setPosterUrl(request.getPosterUrl());
        votable.setImagenUrl(request.getImagenUrl());
        return mapVotableToResponse(contenidoVotableRepository.save(votable));
    }

    public void desactivarVotable(Long id) {
        ContenidoVotable votable = contenidoVotableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contenido votable no encontrado"));
        votable.setActivo(false);
        contenidoVotableRepository.save(votable);
    }

    public List<VotoResponse> verRankingVotos() {
        return peticionRepository.contarVotosPorContenido()
                .stream()
                .map(row -> VotoResponse.builder()
                        .contenidoVotableId((Long) row[0])
                        .titulo((String) row[1])
                        .totalVotos((Long) row[2])
                        .build())
                .toList();
    }

    // ── CLIENTE ────────────────────────────────────────

    public List<ContenidoVotableResponse> listarVotables() {
        return contenidoVotableRepository.findByActivoTrue()
                .stream()
                .map(this::mapVotableToResponse)
                .toList();
    }

    // ← ÚNICO MÉTODO QUE CAMBIÓ
    public PeticionResponse elegirPelicula(String email, PeticionRequest request) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        ContenidoVotable votable = contenidoVotableRepository.findById(request.getContenidoVotableId())
                .orElseThrow(() -> new RuntimeException("Contenido votable no encontrado"));

        Optional<Peticion> existente = peticionRepository.findByUsuarioId(usuario.getId());

        Peticion peticion;
        if (existente.isPresent()) {
            peticion = existente.get();
            peticion.setContenidoVotable(votable);
            peticion.setFechaPeticion(LocalDateTime.now());
        } else {
            peticion = Peticion.builder()
                    .usuario(usuario)
                    .contenidoVotable(votable)
                    .fechaPeticion(LocalDateTime.now())
                    .build();
        }

        return mapPeticionToResponse(peticionRepository.save(peticion));
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