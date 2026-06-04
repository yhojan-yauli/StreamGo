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
                .estado(EstadoContenido.ACTIVO)
                .build();

        return mapToResponse(contenidoRepository.save(contenido));
    }

    public List<ContenidoResponse> listarAdmin() {
        return contenidoRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<ContenidoResponse> listarActivos() {
        return contenidoRepository.findByEstado(EstadoContenido.ACTIVO)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /// Actualiza los datos del contenido desde el CRUD del administrador.
    /// Aquí el admin puede cambiar portada, video, categoría, si es gratuito,
    /// y también marcarlo manualmente como recomendado o tendencia.
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

        return mapToResponse(contenidoRepository.save(contenido));
    }

    // Desactivación lógica: no borra el contenido de la base de datos,
    // solo lo oculta para los clientes.
    public void desactivarContenido(Long id) {

        Contenido contenido = contenidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contenido no encontrado"));

        contenido.setEstado(EstadoContenido.INACTIVO);

        contenidoRepository.save(contenido);
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
                .promedioCalificacion(contenido.getPromedioCalificacion())
                .totalCalificaciones(contenido.getTotalCalificaciones())
                .totalReproducciones(contenido.getTotalReproducciones())
                .build();
    }

    public List<ContenidoResponse> listarPorCategoria(String categoria) {
    return contenidoRepository.findByCategoriaAndEstado(
                    categoria,
                    EstadoContenido.ACTIVO
            )
            .stream()
            .map(this::mapToResponse)
            .toList();
}

public List<ContenidoResponse> listarRecomendados() {
    return contenidoRepository.findByRecomendadoTrueAndEstado(
                    EstadoContenido.ACTIVO
            )
            .stream()
            .map(this::mapToResponse)
            .toList();
}

public List<ContenidoResponse> listarTendencias() {
    return contenidoRepository.findByTendenciaTrueAndEstado(
                    EstadoContenido.ACTIVO
            )
            .stream()
            .map(this::mapToResponse)
            .toList();
}

public List<ContenidoResponse> buscarPorTitulo(String titulo) {
    return contenidoRepository.findByTituloContainingIgnoreCaseAndEstado(
                    titulo,
                    EstadoContenido.ACTIVO
            )
            .stream()
            .map(this::mapToResponse)
            .toList();
}


}