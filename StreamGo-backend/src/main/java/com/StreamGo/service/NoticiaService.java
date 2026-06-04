package com.StreamGo.service;

import com.StreamGo.dto.request.NoticiaRequest;
import com.StreamGo.dto.response.NoticiaResponse;
import com.StreamGo.entity.Noticia;
import com.StreamGo.entity.Usuario;
import com.StreamGo.repository.NoticiaRepository;
import com.StreamGo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio encargado de gestionar la lógica de negocio para la entidad {@link Noticia}.
 * Contiene las operaciones CRUD y reglas adicionales para la administración de noticias.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NoticiaService {

    private final NoticiaRepository noticiaRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Crea una nueva noticia en el sistema.
     *
     * @param request Objeto que contiene los datos de la noticia a crear.
     * @return {@link NoticiaResponse} con los datos de la noticia creada.
     */
    @Transactional
    public NoticiaResponse crearNoticia(NoticiaRequest request) {
        log.info("Iniciando la creación de una nueva noticia con título: {}", request.getTitulo());

        validarRequest(request);

        Usuario autor = obtenerUsuario(request.getIdAutor(), "Autor no encontrado");
        Usuario usuario = obtenerUsuario(request.getIdUsuario(), "Usuario no encontrado");

        Noticia noticia = Noticia.builder()
                .autor(autor)
                .usuario(usuario)
                .titulo(request.getTitulo().trim())
                .reacciones(request.getReacciones() == null ? 0 : request.getReacciones())
                .trailer(normalizarTextoOpcional(request.getTrailer()))
                .contenido(request.getContenido().trim())
                // .fijado(false) es el valor por defecto gracias a @Builder.Default en la entidad
                .build();

        Noticia noticiaGuardada = noticiaRepository.save(noticia);
        log.info("Noticia creada exitosamente con ID: {}", noticiaGuardada.getIdPost());

        return convertirAResponse(noticiaGuardada);
    }

    /**
     * Obtiene una lista de todas las noticias registradas sin un orden específico.
     *
     * @return Lista de {@link NoticiaResponse}.
     */
    @Transactional(readOnly = true)
    public List<NoticiaResponse> listarNoticias() {
        log.info("Consultando la lista completa de noticias");
        return noticiaRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    /**
     * Obtiene una noticia específica a partir de su identificador.
     *
     * @param idPost Identificador único de la noticia.
     * @return {@link NoticiaResponse} con los datos de la noticia encontrada.
     */
    @Transactional(readOnly = true)
    public NoticiaResponse obtenerNoticia(Long idPost) {
        log.info("Consultando noticia por ID: {}", idPost);
        return convertirAResponse(buscarNoticia(idPost));
    }

    /**
     * Lista todas las noticias escritas por un autor específico.
     *
     * @param idAutor Identificador del autor.
     * @return Lista de {@link NoticiaResponse} asociadas al autor.
     */
    @Transactional(readOnly = true)
    public List<NoticiaResponse> listarPorAutor(Long idAutor) {
        log.info("Consultando noticias filtradas por el autor ID: {}", idAutor);
        return noticiaRepository.findByAutorId(idAutor)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    /**
     * Lista todas las noticias asociadas a un usuario en específico.
     *
     * @param idUsuario Identificador del usuario.
     * @return Lista de {@link NoticiaResponse} asociadas al usuario.
     */
    @Transactional(readOnly = true)
    public List<NoticiaResponse> listarPorUsuario(Long idUsuario) {
        log.info("Consultando noticias filtradas por el usuario ID: {}", idUsuario);
        return noticiaRepository.findByUsuarioId(idUsuario)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    /**
     * Actualiza la información de una noticia existente.
     *
     * @param idPost Identificador de la noticia a actualizar.
     * @param request Objeto con los nuevos datos a persistir.
     * @return {@link NoticiaResponse} con los datos actualizados.
     */
    @Transactional
    public NoticiaResponse actualizarNoticia(Long idPost, NoticiaRequest request) {
        log.info("Iniciando actualización de la noticia con ID: {}", idPost);
        validarRequest(request);

        Noticia noticia = buscarNoticia(idPost);

        Usuario autor = obtenerUsuario(request.getIdAutor(), "Autor no encontrado");
        Usuario usuario = obtenerUsuario(request.getIdUsuario(), "Usuario no encontrado");

        noticia.setAutor(autor);
        noticia.setUsuario(usuario);
        noticia.setTitulo(request.getTitulo().trim());
        noticia.setReacciones(request.getReacciones() == null ? noticia.getReacciones() : request.getReacciones());
        noticia.setTrailer(normalizarTextoOpcional(request.getTrailer()));
        noticia.setContenido(request.getContenido().trim());

        Noticia noticiaActualizada = noticiaRepository.save(noticia);
        log.info("Noticia con ID: {} actualizada correctamente", idPost);

        return convertirAResponse(noticiaActualizada);
    }

    /**
     * Incrementa en uno el contador de reacciones de una noticia.
     *
     * @param idPost Identificador de la noticia a reaccionar.
     * @return {@link NoticiaResponse} con el número de reacciones actualizado.
     */
    @Transactional
    public NoticiaResponse reaccionar(Long idPost) {
        log.info("Añadiendo una reacción a la noticia con ID: {}", idPost);
        Noticia noticia = buscarNoticia(idPost);

        int reaccionesActuales = noticia.getReacciones() == null ? 0 : noticia.getReacciones();
        noticia.setReacciones(reaccionesActuales + 1);

        return convertirAResponse(noticiaRepository.save(noticia));
    }

    /**
     * Elimina una noticia del sistema.
     *
     * @param idPost Identificador de la noticia a eliminar.
     */
    @Transactional
    public void eliminarNoticia(Long idPost) {
        log.info("Iniciando proceso de eliminación para la noticia con ID: {}", idPost);
        Noticia noticia = buscarNoticia(idPost);
        noticiaRepository.delete(noticia);
        log.info("Noticia con ID: {} eliminada exitosamente", idPost);
    }

    /**
     * Obtiene una lista de noticias ordenadas dando prioridad a las que están fijadas,
     * y secundariamente por el número de reacciones en orden descendente.
     *
     * @return Lista de {@link NoticiaResponse} ordenadas.
     */
    @Transactional(readOnly = true)
    public List<NoticiaResponse> listarNoticiasOrdenadas() {
        log.info("Consultando noticias ordenadas (Fijadas primero, luego por reacciones)");
        return noticiaRepository.findAllByOrderByFijadoDescReaccionesDesc()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    /**
     * Alterna el estado fijado de una noticia. Si estaba fijada pasa a no fijada, y viceversa.
     *
     * @param idPost Identificador de la noticia.
     * @return {@link NoticiaResponse} con el estado de fijación modificado.
     */
    @Transactional
    public NoticiaResponse fijarNoticia(Long idPost) {
        log.info("Alternando estado de fijado para la noticia con ID: {}", idPost);
        Noticia noticia = buscarNoticia(idPost);

        noticia.setFijado(!noticia.isFijado());

        log.info("Noticia ID: {} actualizada. Estado fijado: {}", idPost, noticia.isFijado());
        return convertirAResponse(noticiaRepository.save(noticia));
    }

    /**
     * Busca una entidad Noticia en la base de datos o lanza una excepción si no existe.
     *
     * @param idPost Identificador de la noticia.
     * @return Entidad {@link Noticia}.
     * @throws RuntimeException si la noticia no es encontrada.
     */
    private Noticia buscarNoticia(Long idPost) {
        return noticiaRepository.findById(idPost)
                .orElseThrow(() -> {
                    log.warn("No se encontró la noticia solicitada con ID: {}", idPost);
                    return new RuntimeException("Noticia no encontrada");
                });
    }

    /**
     * Obtiene una entidad Usuario de la base de datos o lanza una excepción con un mensaje personalizado.
     *
     * @param idUsuario Identificador del usuario.
     * @param mensajeError Mensaje a mostrar si no se encuentra.
     * @return Entidad {@link Usuario}.
     * @throws RuntimeException si el usuario no es encontrado.
     */
    private Usuario obtenerUsuario(Long idUsuario, String mensajeError) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> {
                    log.error("Fallo de búsqueda de entidad: ID {}, Motivo: {}", idUsuario, mensajeError);
                    return new RuntimeException(mensajeError);
                });
    }

    /**
     * Valida que los datos obligatorios dentro del DTO request estén presentes y sean correctos.
     *
     * @param request Objeto {@link NoticiaRequest} con los datos a validar.
     * @throws RuntimeException si alguna validación falla.
     */
    private void validarRequest(NoticiaRequest request) {
        if (request.getIdAutor() == null) {
            log.warn("Intento fallido de operación: El ID de autor es nulo");
            throw new RuntimeException("El autor es obligatorio");
        }
        if (request.getIdUsuario() == null) {
            log.warn("Intento fallido de operación: El ID de usuario es nulo");
            throw new RuntimeException("El usuario es obligatorio");
        }
        if (esTextoVacio(request.getTitulo())) {
            log.warn("Intento fallido de operación: El título está vacío");
            throw new RuntimeException("El título es obligatorio");
        }
        if (esTextoVacio(request.getContenido())) {
            log.warn("Intento fallido de operación: El contenido está vacío");
            throw new RuntimeException("El contenido es obligatorio");
        }
        if (request.getReacciones() != null && request.getReacciones() < 0) {
            log.warn("Intento fallido de operación: Se enviaron reacciones negativas ({})", request.getReacciones());
            throw new RuntimeException("Las reacciones no pueden ser negativas");
        }
    }

    /**
     * Comprueba si una cadena de texto es nula o solo contiene espacios en blanco.
     *
     * @param valor Cadena de texto a evaluar.
     * @return true si es nula o vacía, false en caso contrario.
     */
    private boolean esTextoVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    /**
     * Normaliza un texto opcional eliminando espacios en blanco en los extremos, o retorna nulo si estaba vacío.
     *
     * @param valor Cadena de texto a normalizar.
     * @return Cadena normalizada o nulo.
     */
    private String normalizarTextoOpcional(String valor) {
        return esTextoVacio(valor) ? null : valor.trim();
    }

    /**
     * Mapea una entidad {@link Noticia} a su representación de transferencia {@link NoticiaResponse}.
     *
     * @param noticia Entidad a convertir.
     * @return Objeto {@link NoticiaResponse}.
     */
    private NoticiaResponse convertirAResponse(Noticia noticia) {
        return NoticiaResponse.builder()
                .idPost(noticia.getIdPost())
                .idAutor(noticia.getAutor().getId())
                .autorNombre(noticia.getAutor().getNombre())
                .idUsuario(noticia.getUsuario().getId())
                .usuarioNombre(noticia.getUsuario().getNombre())
                .titulo(noticia.getTitulo())
                .reacciones(noticia.getReacciones())
                .trailer(noticia.getTrailer())
                .contenido(noticia.getContenido())
                .fijado(noticia.isFijado())
                .build();
    }
}