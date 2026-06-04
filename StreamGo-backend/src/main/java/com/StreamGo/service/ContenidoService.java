package com.StreamGo.service;

import com.StreamGo.dto.request.ActualizarContenidoRequest;
import com.StreamGo.dto.request.CrearContenidoRequest;
import com.StreamGo.dto.response.ContenidoResponse;
import com.StreamGo.entity.Contenido;
import com.StreamGo.entity.Enum.EstadoContenido;
import com.StreamGo.repository.ContenidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * Servicio encargado de la lógica de negocio relacionada con contenidos.
 *
 * Gestiona la creación, actualización, listado, búsqueda,
 * filtrado y cambio de estado de los contenidos de StreamGo.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContenidoService {

    private final ContenidoRepository contenidoRepository;
/**
 * Crea un nuevo contenido dentro de la plataforma.
 *
 * @param request datos del contenido a crear.
 * @return respuesta con los datos del contenido creado.
 */
    public ContenidoResponse crearContenido(CrearContenidoRequest request) {

        log.info("Intentando crear nuevo contenido: {}", request.getTitulo());

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

        ContenidoResponse response = mapToResponse(contenidoRepository.save(contenido));
        
        log.info("Contenido creado exitosamente. ID: {}, Título: {}, Estado: {}", 
                response.getId(), response.getTitulo(), response.getEstado());

        return response;
    }
/**
 * Lista todos los contenidos para uso administrativo.
 *
 * @return lista completa de contenidos.
 */
    public List<ContenidoResponse> listarAdmin() {
        log.debug("Listando todos los contenidos para administrador");
        
        List<ContenidoResponse> contenidos = contenidoRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
        
        log.debug("Total de contenidos listados para admin: {}", contenidos.size());
        return contenidos;
    }

    // Público: usuario sin login
/**
 * Lista los contenidos disponibles para usuarios no autenticados.
 *
 * @return lista de contenidos con estado SINLOGIN.
 */
    public List<ContenidoResponse> listarSinLogin() {
        log.debug("Listando contenidos públicos (SINLOGIN)");
        
        List<ContenidoResponse> contenidos = contenidoRepository.findByEstado(EstadoContenido.SINLOGIN)
                .stream()
                .map(this::mapToResponse)
                .toList();
        
        log.debug("Contenidos públicos encontrados: {}", contenidos.size());
        return contenidos;
    }

    // Cliente sin suscripción: puede ver INACTIVO y SINLOGIN
/**
 * Lista contenidos disponibles para clientes sin suscripción.
 *
 * Incluye contenidos INACTIVOS y SINLOGIN.
 *
 * @return lista de contenidos disponibles.
 */
    public List<ContenidoResponse> listarParaClienteSinSuscripcion() {
        log.debug("Listando contenidos para cliente sin suscripción (INACTIVO y SINLOGIN)");
        
        List<ContenidoResponse> contenidos = contenidoRepository.findAll()
                .stream()
                .filter(c ->
                        c.getEstado() == EstadoContenido.INACTIVO ||
                        c.getEstado() == EstadoContenido.SINLOGIN
                )
                .map(this::mapToResponse)
                .toList();
        
        log.debug("Contenidos disponibles para cliente sin suscripción: {}", contenidos.size());
        return contenidos;
    }

    // Cliente con suscripción: puede ver todo
/**
 * Lista todos los contenidos disponibles para clientes con suscripción.
 *
 * @return catálogo completo de contenidos.
 */
    public List<ContenidoResponse> listarParaClienteConSuscripcion() {
        log.debug("Listando todos los contenidos para cliente con suscripción");
        
        List<ContenidoResponse> contenidos = contenidoRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
        
        log.debug("Total de contenidos listados para cliente con suscripción: {}", contenidos.size());
        return contenidos;
    }

    /**
     * Actualiza la información de un contenido existente.
     *
     * @param id identificador del contenido a actualizar.
     * @param request datos actualizados del contenido.
     * @return respuesta con los datos del contenido actualizado.
     */
    public ContenidoResponse actualizarContenido(
            Long id,
            ActualizarContenidoRequest request
    ) {
        log.info("Intentando actualizar contenido con ID: {}", id);
        
        Contenido contenido = contenidoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Contenido no encontrado con ID: {}", id);
                    return new RuntimeException("Contenido no encontrado");
                });

        String tituloAnterior = contenido.getTitulo();
        
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

        ContenidoResponse response = mapToResponse(contenidoRepository.save(contenido));
        
        log.info("Contenido actualizado. ID: {}, Título anterior: '{}', Título nuevo: '{}', Estado: {}", 
                id, tituloAnterior, response.getTitulo(), response.getEstado());

        return response;
    }

    // Ahora no significa borrar, sino cambiar el acceso del contenido.
/**
 * Cambia el estado de un contenido.
 *
 * @param id identificador del contenido.
 * @param estado nuevo estado del contenido.
 */
    public void cambiarEstadoContenido(Long id, EstadoContenido estado) {
        log.info("Cambiando estado del contenido ID: {} al estado: {}", id, estado);
        
        Contenido contenido = contenidoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Contenido no encontrado con ID: {}", id);
                    return new RuntimeException("Contenido no encontrado");
                });

        EstadoContenido estadoAnterior = contenido.getEstado();
        contenido.setEstado(estado);
        contenidoRepository.save(contenido);
        
        log.info("Estado cambiado exitosamente. Contenido ID: {}, Título: '{}', Estado anterior: {}, Estado nuevo: {}", 
                id, contenido.getTitulo(), estadoAnterior, estado);
    }
/**
 * Desactiva un contenido cambiando su estado a INACTIVO.
 *
 * @param id identificador del contenido.
 */
    public void desactivarContenido(Long id) {
        log.info("Desactivando contenido con ID: {}", id);
        cambiarEstadoContenido(id, EstadoContenido.INACTIVO);
        log.info("Contenido ID: {} desactivado correctamente", id);
    }
/**
 * Filtra contenidos por categoría.
 *
 * @param categoria categoría buscada.
 * @return lista de contenidos pertenecientes a la categoría.
 */
    public List<ContenidoResponse> listarPorCategoria(String categoria) {
        log.debug("Buscando contenidos por categoría: {}", categoria);
        
        List<ContenidoResponse> contenidos = contenidoRepository.findAll()
                .stream()
                .filter(c -> c.getCategoria() != null &&
                        c.getCategoria().equalsIgnoreCase(categoria))
                .map(this::mapToResponse)
                .toList();
        
        log.debug("Contenidos encontrados en categoría '{}': {}", categoria, contenidos.size());
        return contenidos;
    }
/**
 * Lista los contenidos recomendados.
 *
 * @return lista de contenidos marcados como recomendados.
 */
    public List<ContenidoResponse> listarRecomendados() {
        log.debug("Listando contenidos recomendados");
        
        List<ContenidoResponse> contenidos = contenidoRepository.findAll()
                .stream()
                .filter(c -> Boolean.TRUE.equals(c.getRecomendado()))
                .map(this::mapToResponse)
                .toList();
        
        log.debug("Contenidos recomendados encontrados: {}", contenidos.size());
        return contenidos;
    }
/**
 * Lista los contenidos en tendencia.
 *
 * @return lista de contenidos marcados como tendencia.
 */
    public List<ContenidoResponse> listarTendencias() {
        log.debug("Listando contenidos en tendencia");
        
        List<ContenidoResponse> contenidos = contenidoRepository.findAll()
                .stream()
                .filter(c -> Boolean.TRUE.equals(c.getTendencia()))
                .map(this::mapToResponse)
                .toList();
        
        log.debug("Contenidos en tendencia encontrados: {}", contenidos.size());
        return contenidos;
    }
/**
 * Busca contenidos por título.
 *
 * @param titulo texto usado en la búsqueda.
 * @return lista de contenidos encontrados.
 */
    public List<ContenidoResponse> buscarPorTitulo(String titulo) {
        log.debug("Buscando contenidos por título: {}", titulo);
        
        List<ContenidoResponse> contenidos = contenidoRepository.findAll()
                .stream()
                .filter(c -> c.getTitulo() != null &&
                        c.getTitulo().toLowerCase().contains(titulo.toLowerCase()))
                .map(this::mapToResponse)
                .toList();
        
        log.debug("Contenidos encontrados con título que contiene '{}': {}", titulo, contenidos.size());
        return contenidos;
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
    
    //admin puede eliminar contenido, lo que realmente hace es borrarlo de la base de datos. Solo se recomienda usar esta función para eliminar contenido que se haya creado por error o que ya no se quiera mostrar en la plataforma, pero que no se quiera mantener un registro histórico de su existencia.
    public void eliminarContenido(Long id) {
        log.warn("Intentando eliminar contenido con ID: {}", id);
        
        Contenido contenido = contenidoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Intento de eliminar contenido no existente con ID: {}", id);
                    return new RuntimeException("Contenido no encontrado");
                });

        String titulo = contenido.getTitulo();
        contenidoRepository.delete(contenido);
        
        log.warn("Contenido eliminado permanentemente. ID: {}, Título: '{}'", id, titulo);
    }
}