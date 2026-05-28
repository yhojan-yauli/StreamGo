package com.StreamGo.service;

import com.StreamGo.dto.request.ActualizarContenidoRequest;
import com.StreamGo.dto.request.CrearContenidoRequest;
import com.StreamGo.dto.response.ContenidoResponse;
import com.StreamGo.entity.Contenido;
import com.StreamGo.entity.Enum.EstadoContenido;
import com.StreamGo.repository.ContenidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContenidoService {

    private final ContenidoRepository contenidoRepository;

    public ContenidoResponse crearContenido(CrearContenidoRequest request) {

        Contenido contenido = Contenido.builder()
                .titulo(request.getTitulo())
                .descripcion(request.getDescripcion())
                .categoria(request.getCategoria())
                .tipoContenido(request.getTipoContenido())
                .imagenUrl(request.getImagenUrl())
                .bannerUrl(request.getBannerUrl())
                .videoUrl(request.getVideoUrl())
                .fechaEstreno(request.getFechaEstreno())
                .duracionMinutos(request.getDuracionMinutos())
                .gratuito(request.getGratuito())
                .recomendado(request.getRecomendado())
                .tendencia(request.getTendencia())
                .promedioCalificacion(0.0)
                .totalCalificaciones(0)
                .totalReproducciones(0)
                .estado(request.getEstado() != null ? request.getEstado() : EstadoContenido.ACTIVO)
                .build();

        return mapToResponse(contenidoRepository.save(contenido));
    }

    public List<ContenidoResponse> listarAdmin() {
        return contenidoRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Público: usuario sin login
    public List<ContenidoResponse> listarSinLogin() {
        return contenidoRepository.findByEstado(EstadoContenido.SINLOGIN)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Cliente sin suscripción: puede ver INACTIVO y SINLOGIN
    public List<ContenidoResponse> listarParaClienteSinSuscripcion() {
        return contenidoRepository.findAll()
                .stream()
                .filter(c ->
                        c.getEstado() == EstadoContenido.INACTIVO ||
                        c.getEstado() == EstadoContenido.SINLOGIN
                )
                .map(this::mapToResponse)
                .toList();
    }

    // Cliente con suscripción: puede ver todo
    public List<ContenidoResponse> listarParaClienteConSuscripcion() {
        return contenidoRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ContenidoResponse actualizarContenido(
            Long id,
            ActualizarContenidoRequest request
    ) {
        Contenido contenido = contenidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contenido no encontrado"));

        contenido.setTitulo(request.getTitulo());
        contenido.setDescripcion(request.getDescripcion());
        contenido.setCategoria(request.getCategoria());
        contenido.setTipoContenido(request.getTipoContenido());
        contenido.setImagenUrl(request.getImagenUrl());
        contenido.setBannerUrl(request.getBannerUrl());
        contenido.setVideoUrl(request.getVideoUrl());
        contenido.setFechaEstreno(request.getFechaEstreno());
        contenido.setDuracionMinutos(request.getDuracionMinutos());
        contenido.setGratuito(request.getGratuito());
        contenido.setRecomendado(request.getRecomendado());
        contenido.setTendencia(request.getTendencia());

        if (request.getEstado() != null) {
            contenido.setEstado(request.getEstado());
        }

        return mapToResponse(contenidoRepository.save(contenido));
    }

    // Ahora no significa borrar, sino cambiar el acceso del contenido.
    public void cambiarEstadoContenido(Long id, EstadoContenido estado) {

        Contenido contenido = contenidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contenido no encontrado"));

        contenido.setEstado(estado);
        contenidoRepository.save(contenido);
    }

    public void desactivarContenido(Long id) {
        cambiarEstadoContenido(id, EstadoContenido.INACTIVO);
    }

    public List<ContenidoResponse> listarPorCategoria(String categoria) {
        return contenidoRepository.findAll()
                .stream()
                .filter(c -> c.getCategoria() != null &&
                        c.getCategoria().equalsIgnoreCase(categoria))
                .map(this::mapToResponse)
                .toList();
    }

    public List<ContenidoResponse> listarRecomendados() {
        return contenidoRepository.findAll()
                .stream()
                .filter(c -> Boolean.TRUE.equals(c.getRecomendado()))
                .map(this::mapToResponse)
                .toList();
    }

    public List<ContenidoResponse> listarTendencias() {
        return contenidoRepository.findAll()
                .stream()
                .filter(c -> Boolean.TRUE.equals(c.getTendencia()))
                .map(this::mapToResponse)
                .toList();
    }

    public List<ContenidoResponse> buscarPorTitulo(String titulo) {
        return contenidoRepository.findAll()
                .stream()
                .filter(c -> c.getTitulo() != null &&
                        c.getTitulo().toLowerCase().contains(titulo.toLowerCase()))
                .map(this::mapToResponse)
                .toList();
    }

    private ContenidoResponse mapToResponse(Contenido contenido) {
        return ContenidoResponse.builder()
                .id(contenido.getId())
                .titulo(contenido.getTitulo())
                .descripcion(contenido.getDescripcion())
                .categoria(contenido.getCategoria())
                .tipoContenido(contenido.getTipoContenido())
                .imagenUrl(contenido.getImagenUrl())
                .bannerUrl(contenido.getBannerUrl())
                .videoUrl(contenido.getVideoUrl())
                .fechaEstreno(contenido.getFechaEstreno())
                .duracionMinutos(contenido.getDuracionMinutos())
                .gratuito(contenido.getGratuito())
                .recomendado(contenido.getRecomendado())
                .tendencia(contenido.getTendencia())
                .estado(contenido.getEstado())
                .promedioCalificacion(contenido.getPromedioCalificacion())
                .totalCalificaciones(contenido.getTotalCalificaciones())
                .totalReproducciones(contenido.getTotalReproducciones())
                .build();
    }
}