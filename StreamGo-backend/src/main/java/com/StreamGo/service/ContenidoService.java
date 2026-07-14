package com.StreamGo.service;

import com.StreamGo.dto.request.ActualizarContenidoRequest;
import com.StreamGo.dto.request.CrearContenidoRequest;
import com.StreamGo.dto.response.ContenidoResponse;
import com.StreamGo.entity.Contenido;
import com.StreamGo.entity.Enum.EstadoContenido;
import com.StreamGo.repository.ContenidoRepository;
import com.StreamGo.repository.UsuarioRepository;
import com.StreamGo.repository.SuscripcionRepository;
import com.StreamGo.entity.Usuario;
import com.StreamGo.entity.Suscripcion;
import com.StreamGo.entity.Enum.EstadoUsuario;
import com.StreamGo.entity.Enum.EstadoSuscripcion;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    private final UsuarioRepository usuarioRepository;
    private final SuscripcionService suscripcionService;
    private final FileStorageService fileStorageService; // <-- AGREGADO

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
     * Crea un nuevo contenido con archivos (imagen, banner, video).
     * Los archivos se suben al servidor remoto a través de FileStorageService.
     *
     * @param request datos del contenido a crear.
     * @param imagen archivo de imagen (poster)
     * @param banner archivo de banner
     * @param video archivo de video
     * @return respuesta con los datos del contenido creado.
     */
    public ContenidoResponse crearContenidoConArchivos(
            CrearContenidoRequest request,
            MultipartFile imagen,
            MultipartFile banner,
            MultipartFile video
    ) {
        log.info("Creando contenido con archivos: {}", request.getTitulo());
        
        // Primero crear el contenido sin archivos
        ContenidoResponse response = crearContenido(request);
        Long id = response.getId();
        
        // Sanitizar el título para usar como nombre base
        String nombreBase = fileStorageService.sanitizarTitulo(request.getTitulo());

        // Guardar los archivos en el servidor remoto
        String imagenUrl = fileStorageService.guardarImagen(imagen, nombreBase, "poster");
        String bannerUrl = fileStorageService.guardarImagen(banner, nombreBase, "banner");
        String videoUrl = fileStorageService.guardarVideo(video, nombreBase);

        // Actualizar el contenido con las URLs de los archivos
        Contenido contenido = contenidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contenido no encontrado"));

        if (imagenUrl != null) contenido.setImagenUrl(imagenUrl);
        if (bannerUrl != null) contenido.setBannerUrl(bannerUrl);
        if (videoUrl != null) contenido.setVideoUrl(videoUrl);

        ContenidoResponse finalResponse = mapToResponse(contenidoRepository.save(contenido));
        
        log.info("Contenido creado con archivos. ID: {}, Título: {}", finalResponse.getId(), finalResponse.getTitulo());
        
        return finalResponse;
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

    /**
     * Lista contenidos disponibles para clientes sin suscripción.
     *
     * Incluye contenidos INACTIVOS y SINLOGIN.
     *
     * @return lista de contenidos disponibles.
     */
    public List<ContenidoResponse> listarParaClienteSinSuscripcion() {
        log.debug("Listando contenidos para cliente sin suscripción (INACTIVO y SINLOGIN)");
        
        List<ContenidoResponse> contenidos = contenidoRepository.findByEstadoIn(
                        java.util.List.of(EstadoContenido.INACTIVO, EstadoContenido.SINLOGIN))
                .stream()
                .map(this::mapToResponse)
                .toList();
        
        log.debug("Contenidos disponibles para cliente sin suscripción: {}", contenidos.size());
        return contenidos;
    }

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
     * Lista contenidos para un usuario autenticado según su estado y suscripción.
     *
     * Reglas:
     * - Usuario ACTIVO o con suscripción activa: ve ACTIVO, INACTIVO y SINLOGIN.
     * - Usuario INACTIVO: ve INACTIVO y SINLOGIN.
     * - Usuario SUSPENDIDO: no visualiza contenidos.
     *
     * @param email correo del usuario autenticado.
     * @return lista de contenidos permitidos para el usuario.
     */
    public List<ContenidoResponse> listarParaUsuario(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getEstado() == EstadoUsuario.SUSPENDIDO) {
            log.warn("Usuario suspendido {} intentó listar contenidos", email);
            throw new RuntimeException("Tu cuenta se encuentra suspendida");
        }

        boolean accesoTotal = usuario.getEstado() == EstadoUsuario.ACTIVO || tieneSuscripcionActiva(usuario);

        if (accesoTotal) {
            return listarParaClienteConSuscripcion();
        }

        return listarParaClienteSinSuscripcion();
    }

    /**
     * Verifica si el usuario tiene una suscripción activa y vigente.
     *
     * @param usuario usuario a validar.
     * @return true si posee suscripción activa, false en caso contrario.
     */
    private boolean tieneSuscripcionActiva(Usuario usuario) {
        return suscripcionService.usuarioTieneSuscripcionActiva(usuario);
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

    /**
     * Actualiza un contenido existente con archivos (imagen, banner, video).
     * Los archivos se suben al servidor remoto a través de FileStorageService.
     *
     * @param id identificador del contenido a actualizar.
     * @param request datos actualizados del contenido.
     * @param imagen archivo de imagen (poster) - opcional
     * @param banner archivo de banner - opcional
     * @param video archivo de video - opcional
     * @return respuesta con los datos del contenido actualizado.
     */
    public ContenidoResponse actualizarContenidoConArchivos(
            Long id,
            ActualizarContenidoRequest request,
            MultipartFile imagen,
            MultipartFile banner,
            MultipartFile video
    ) {
        log.info("Actualizando contenido con archivos. ID: {}", id);
        
        Contenido contenido = contenidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contenido no encontrado"));

        String nombreBase = fileStorageService.sanitizarTitulo(request.getTitulo());

        // Actualizar imagen si se proporciona
        if (imagen != null && !imagen.isEmpty()) {
            // Eliminar archivo anterior
            if (contenido.getImagenUrl() != null) {
                fileStorageService.eliminarArchivo(contenido.getImagenUrl());
            }
            // Guardar nueva imagen
            String nuevaImagenUrl = fileStorageService.guardarImagen(imagen, nombreBase, "poster");
            contenido.setImagenUrl(nuevaImagenUrl);
        } else if (request.getImagenUrl() != null) {
            contenido.setImagenUrl(request.getImagenUrl());
        }

        // Actualizar banner si se proporciona
        if (banner != null && !banner.isEmpty()) {
            if (contenido.getBannerUrl() != null) {
                fileStorageService.eliminarArchivo(contenido.getBannerUrl());
            }
            String nuevoBannerUrl = fileStorageService.guardarImagen(banner, nombreBase, "banner");
            contenido.setBannerUrl(nuevoBannerUrl);
        } else if (request.getBannerUrl() != null) {
            contenido.setBannerUrl(request.getBannerUrl());
        }

        // Actualizar video si se proporciona
        if (video != null && !video.isEmpty()) {
            if (contenido.getVideoUrl() != null) {
                fileStorageService.eliminarArchivo(contenido.getVideoUrl());
            }
            String nuevoVideoUrl = fileStorageService.guardarVideo(video, nombreBase);
            contenido.setVideoUrl(nuevoVideoUrl);
        } else if (request.getVideoUrl() != null) {
            contenido.setVideoUrl(request.getVideoUrl());
        }

        // Actualizar otros campos
        contenido.setTitulo(request.getTitulo());
        contenido.setDescripcion(request.getDescripcion());
        contenido.setCategoria(request.getCategoria());
        contenido.setTipoContenido(request.getTipoContenido());
        contenido.setFechaEstreno(request.getFechaEstreno());
        contenido.setDuracionMinutos(request.getDuracionMinutos());
        contenido.setGratuito(request.getGratuito());
        contenido.setRecomendado(request.getRecomendado());
        contenido.setTendencia(request.getTendencia());

        if (request.getEstado() != null) {
            contenido.setEstado(request.getEstado());
        }

        ContenidoResponse response = mapToResponse(contenidoRepository.save(contenido));
        
        log.info("Contenido actualizado con archivos. ID: {}, Título: {}", response.getId(), response.getTitulo());
        
        return response;
    }

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
        
        List<ContenidoResponse> contenidos = contenidoRepository.findByCategoria(categoria)
                .stream()
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
        
        List<ContenidoResponse> contenidos = contenidoRepository.findByRecomendadoTrue()
                .stream()
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
        
        List<ContenidoResponse> contenidos = contenidoRepository.findByTendenciaTrue()
                .stream()
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
        
        List<ContenidoResponse> contenidos = contenidoRepository.findByTituloContainingIgnoreCase(titulo)
                .stream()
                .map(this::mapToResponse)
                .toList();
        
        log.debug("Contenidos encontrados con título que contiene '{}': {}", titulo, contenidos.size());
        return contenidos;
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
    
    /**
     * Admin puede eliminar contenido, lo que realmente hace es borrarlo de la base de datos.
     * Tambien elimina los archivos asociados del servidor de archivos.
     * Solo se recomienda usar esta función para eliminar contenido que se haya creado por error
     * o que ya no se quiera mostrar en la plataforma.
     */
    public void eliminarContenido(Long id) {
        log.warn("Intentando eliminar contenido con ID: {}", id);
        
        Contenido contenido = contenidoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Intento de eliminar contenido no existente con ID: {}", id);
                    return new RuntimeException("Contenido no encontrado");
                });

        String titulo = contenido.getTitulo();
        
        // Eliminar archivos asociados del servidor remoto
        if (contenido.getImagenUrl() != null) {
            fileStorageService.eliminarArchivo(contenido.getImagenUrl());
        }
        if (contenido.getBannerUrl() != null) {
            fileStorageService.eliminarArchivo(contenido.getBannerUrl());
        }
        if (contenido.getVideoUrl() != null) {
            fileStorageService.eliminarArchivo(contenido.getVideoUrl());
        }
        
        contenidoRepository.delete(contenido);
        
        log.warn("Contenido eliminado permanentemente. ID: {}, Título: '{}'", id, titulo);
    }
}
