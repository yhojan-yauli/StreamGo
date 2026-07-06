package com.StreamGo.service;

import com.StreamGo.dto.query.NoticiaQuery;
import com.StreamGo.dto.request.NoticiaRequest;
import com.StreamGo.dto.response.NoticiaResponse;
import com.StreamGo.dto.response.PageResponse;
import com.StreamGo.entity.Noticia;
import com.StreamGo.entity.Usuario;
import com.StreamGo.repository.NoticiaRepository;
import com.StreamGo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticiaService {

    private final NoticiaRepository noticiaRepository;
    private final UsuarioRepository usuarioRepository;
    private final NoticiaPortadaStorageService portadaStorageService;

    @Transactional
    public NoticiaResponse crearNoticia(NoticiaRequest request) {
        log.info("Iniciando creacion manual de noticia con titulo: {}", request.getTitulo());

        validarRequestConRelaciones(request);

        Usuario autor = obtenerUsuario(request.getIdAutor(), "Autor no encontrado");
        Usuario usuario = obtenerUsuario(request.getIdUsuario(), "Usuario no encontrado");

        Noticia noticia = construirNoticia(request, autor, usuario);
        return convertirAResponse(noticiaRepository.save(noticia));
    }

    @Transactional
    public NoticiaResponse crearNoticia(NoticiaRequest request, String emailAutor) {
        log.info("Iniciando creacion admin de noticia con titulo: {}", request.getTitulo());

        validarContenidoRequest(request);

        Usuario administrador = obtenerUsuarioPorEmail(emailAutor);
        Noticia noticia = construirNoticia(request, administrador, administrador);

        return convertirAResponse(noticiaRepository.save(noticia));
    }

    @Transactional
    public NoticiaResponse crearNoticia(NoticiaRequest request, String emailAutor, MultipartFile portada) {
        if (tieneArchivo(portada)) {
            request.setPortadaUrl(portadaStorageService.guardar(portada));
        }

        return crearNoticia(request, emailAutor);
    }

    @Transactional(readOnly = true)
    public List<NoticiaResponse> listarNoticias() {
        log.info("Consultando la lista completa de noticias");
        return noticiaRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<NoticiaResponse> buscarNoticias(NoticiaQuery query) {
        log.info(
                "Buscando noticias publicas. search={}, estado={}, sort={}, page={}, size={}",
                query.getSearch(),
                query.getEstado(),
                query.getSort(),
                query.getPage(),
                query.getSize()
        );

        return convertirAPaginaResponse(
                noticiaRepository.buscarNoticias(
                        query.getSearch(),
                        query.fijado(),
                        construirPageable(query)
                )
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<NoticiaResponse> buscarNoticiasAdmin(NoticiaQuery query) {
        log.info(
                "Buscando noticias admin. search={}, estado={}, sort={}, page={}, size={}",
                query.getSearch(),
                query.getEstado(),
                query.getSort(),
                query.getPage(),
                query.getSize()
        );

        return buscarNoticias(query);
    }

    @Transactional(readOnly = true)
    public NoticiaResponse obtenerNoticia(Long idPost) {
        log.info("Consultando noticia por ID: {}", idPost);
        return convertirAResponse(buscarNoticia(idPost));
    }

    @Transactional(readOnly = true)
    public List<NoticiaResponse> listarPorAutor(Long idAutor) {
        log.info("Consultando noticias filtradas por el autor ID: {}", idAutor);
        return noticiaRepository.findByAutorId(idAutor)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NoticiaResponse> listarPorUsuario(Long idUsuario) {
        log.info("Consultando noticias filtradas por el usuario ID: {}", idUsuario);
        return noticiaRepository.findByUsuarioId(idUsuario)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional
    public NoticiaResponse actualizarNoticia(Long idPost, NoticiaRequest request) {
        log.info("Iniciando actualizacion de la noticia con ID: {}", idPost);
        validarContenidoRequest(request);

        Noticia noticia = buscarNoticia(idPost);

        if (request.getIdAutor() != null) {
            noticia.setAutor(obtenerUsuario(request.getIdAutor(), "Autor no encontrado"));
        }
        if (request.getIdUsuario() != null) {
            noticia.setUsuario(obtenerUsuario(request.getIdUsuario(), "Usuario no encontrado"));
        }

        noticia.setTitulo(request.getTitulo().trim());
        noticia.setReacciones(request.getReacciones() == null ? noticia.getReacciones() : request.getReacciones());
        noticia.setTrailer(normalizarTextoOpcional(request.getTrailer()));
        if (request.getPortadaUrl() != null) {
            noticia.setPortadaUrl(normalizarTextoOpcional(request.getPortadaUrl()));
        }
        noticia.setContenido(request.getContenido().trim());

        return convertirAResponse(noticiaRepository.save(noticia));
    }

    @Transactional
    public NoticiaResponse actualizarNoticia(Long idPost, NoticiaRequest request, MultipartFile portada) {
        if (tieneArchivo(portada)) {
            request.setPortadaUrl(portadaStorageService.guardar(portada));
        }

        return actualizarNoticia(idPost, request);
    }

    @Transactional
    public NoticiaResponse reaccionar(Long idPost) {
        log.info("Anadiendo una reaccion a la noticia con ID: {}", idPost);
        Noticia noticia = buscarNoticia(idPost);

        int reaccionesActuales = noticia.getReacciones() == null ? 0 : noticia.getReacciones();
        noticia.setReacciones(reaccionesActuales + 1);

        return convertirAResponse(noticiaRepository.save(noticia));
    }

    @Transactional
    public void eliminarNoticia(Long idPost) {
        log.info("Iniciando proceso de eliminacion para la noticia con ID: {}", idPost);
        Noticia noticia = buscarNoticia(idPost);
        noticiaRepository.delete(noticia);
        log.info("Noticia con ID: {} eliminada exitosamente", idPost);
    }

    @Transactional(readOnly = true)
    public List<NoticiaResponse> listarNoticiasOrdenadas() {
        log.info("Consultando noticias ordenadas");
        return noticiaRepository.findAllByOrderByFijadoDescReaccionesDesc()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional
    public NoticiaResponse fijarNoticia(Long idPost) {
        log.info("Alternando estado de fijado para la noticia con ID: {}", idPost);
        Noticia noticia = buscarNoticia(idPost);

        noticia.setFijado(!noticia.isFijado());

        return convertirAResponse(noticiaRepository.save(noticia));
    }

    private Noticia construirNoticia(NoticiaRequest request, Usuario autor, Usuario usuario) {
        return Noticia.builder()
                .autor(autor)
                .usuario(usuario)
                .titulo(request.getTitulo().trim())
                .reacciones(request.getReacciones() == null ? 0 : request.getReacciones())
                .trailer(normalizarTextoOpcional(request.getTrailer()))
                .portadaUrl(normalizarTextoOpcional(request.getPortadaUrl()))
                .contenido(request.getContenido().trim())
                .build();
    }

    private Pageable construirPageable(NoticiaQuery query) {
        return PageRequest.of(query.getPage(), query.getSize(), construirSort(query.getSort()));
    }

    private Sort construirSort(String sort) {
        if (NoticiaQuery.SORT_REACCIONES.equals(sort)) {
            return Sort.by(
                    Sort.Order.desc("reacciones"),
                    Sort.Order.desc("idPost")
            );
        }

        if (NoticiaQuery.SORT_TITULO.equals(sort)) {
            return Sort.by(Sort.Order.asc("titulo"));
        }

        return Sort.by(
                Sort.Order.desc("fechaCreacion"),
                Sort.Order.desc("idPost")
        );
    }

    private PageResponse<NoticiaResponse> convertirAPaginaResponse(Page<Noticia> page) {
        List<NoticiaResponse> content = page.getContent()
                .stream()
                .map(this::convertirAResponse)
                .toList();

        return PageResponse.<NoticiaResponse>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    private Noticia buscarNoticia(Long idPost) {
        return noticiaRepository.findById(idPost)
                .orElseThrow(() -> {
                    log.warn("No se encontro la noticia solicitada con ID: {}", idPost);
                    return new RuntimeException("Noticia no encontrada");
                });
    }

    private Usuario obtenerUsuario(Long idUsuario, String mensajeError) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> {
                    log.error("Fallo de busqueda de usuario: ID {}, Motivo: {}", idUsuario, mensajeError);
                    return new RuntimeException(mensajeError);
                });
    }

    private Usuario obtenerUsuarioPorEmail(String email) {
        if (esTextoVacio(email)) {
            throw new RuntimeException("No se pudo identificar el usuario autenticado");
        }

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("No se encontro usuario autenticado con email: {}", email);
                    return new RuntimeException("Usuario autenticado no encontrado");
                });
    }

    private void validarRequestConRelaciones(NoticiaRequest request) {
        validarContenidoRequest(request);

        if (request.getIdAutor() == null) {
            log.warn("Intento fallido de operacion: El ID de autor es nulo");
            throw new RuntimeException("El autor es obligatorio");
        }
        if (request.getIdUsuario() == null) {
            log.warn("Intento fallido de operacion: El ID de usuario es nulo");
            throw new RuntimeException("El usuario es obligatorio");
        }
    }

    private void validarContenidoRequest(NoticiaRequest request) {
        if (request == null) {
            throw new RuntimeException("Los datos de la noticia son obligatorios");
        }
        if (esTextoVacio(request.getTitulo())) {
            log.warn("Intento fallido de operacion: El titulo esta vacio");
            throw new RuntimeException("El título es obligatorio");
        }
        if (esTextoVacio(request.getContenido())) {
            log.warn("Intento fallido de operacion: El contenido esta vacio");
            throw new RuntimeException("El contenido es obligatorio");
        }
        if (request.getReacciones() != null && request.getReacciones() < 0) {
            log.warn("Intento fallido de operacion: Se enviaron reacciones negativas ({})", request.getReacciones());
            throw new RuntimeException("Las reacciones no pueden ser negativas");
        }
    }

    private boolean esTextoVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private boolean tieneArchivo(MultipartFile archivo) {
        return archivo != null && !archivo.isEmpty();
    }

    private String normalizarTextoOpcional(String valor) {
        return esTextoVacio(valor) ? null : valor.trim();
    }

    private NoticiaResponse convertirAResponse(Noticia noticia) {
        Usuario autor = noticia.getAutor();
        Usuario usuario = noticia.getUsuario();

        return NoticiaResponse.builder()
                .idPost(noticia.getIdPost())
                .idAutor(autor == null ? null : autor.getId())
                .autorNombre(autor == null ? null : autor.getNombre())
                .idUsuario(usuario == null ? null : usuario.getId())
                .usuarioNombre(usuario == null ? null : usuario.getNombre())
                .titulo(noticia.getTitulo())
                .reacciones(noticia.getReacciones())
                .trailer(noticia.getTrailer())
                .portadaUrl(noticia.getPortadaUrl())
                .contenido(noticia.getContenido())
                .fechaCreacion(noticia.getFechaCreacion())
                .fijado(noticia.isFijado())
                .build();
    }
}
