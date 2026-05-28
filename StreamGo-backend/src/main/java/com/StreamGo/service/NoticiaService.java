package com.StreamGo.service;

import com.StreamGo.dto.request.NoticiaRequest;
import com.StreamGo.dto.response.NoticiaResponse;
import com.StreamGo.entity.Noticia;
import com.StreamGo.entity.Usuario;
import com.StreamGo.repository.NoticiaRepository;
import com.StreamGo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticiaService {

    private final NoticiaRepository noticiaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public NoticiaResponse crearNoticia(NoticiaRequest request) {

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
                .build();

        return convertirAResponse(noticiaRepository.save(noticia));
    }

    @Transactional(readOnly = true)
    public List<NoticiaResponse> listarNoticias() {

        return noticiaRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public NoticiaResponse obtenerNoticia(Long idPost) {

        return convertirAResponse(buscarNoticia(idPost));
    }

    @Transactional(readOnly = true)
    public List<NoticiaResponse> listarPorAutor(Long idAutor) {

        return noticiaRepository.findByAutorId(idAutor)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NoticiaResponse> listarPorUsuario(Long idUsuario) {

        return noticiaRepository.findByUsuarioId(idUsuario)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional
    public NoticiaResponse actualizarNoticia(Long idPost, NoticiaRequest request) {

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

        return convertirAResponse(noticiaRepository.save(noticia));
    }

    @Transactional
    public NoticiaResponse reaccionar(Long idPost) {

        Noticia noticia = buscarNoticia(idPost);

        int reaccionesActuales = noticia.getReacciones() == null ? 0 : noticia.getReacciones();
        noticia.setReacciones(reaccionesActuales + 1);

        return convertirAResponse(noticiaRepository.save(noticia));
    }

    @Transactional
    public void eliminarNoticia(Long idPost) {

        Noticia noticia = buscarNoticia(idPost);
        noticiaRepository.delete(noticia);
    }

    private Noticia buscarNoticia(Long idPost) {

        return noticiaRepository.findById(idPost)
                .orElseThrow(() ->
                        new RuntimeException("Noticia no encontrada"));
    }

    private Usuario obtenerUsuario(Long idUsuario, String mensajeError) {

        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() ->
                        new RuntimeException(mensajeError));
    }

    private void validarRequest(NoticiaRequest request) {

        if (request.getIdAutor() == null) {
            throw new RuntimeException("El autor es obligatorio");
        }

        if (request.getIdUsuario() == null) {
            throw new RuntimeException("El usuario es obligatorio");
        }

        if (esTextoVacio(request.getTitulo())) {
            throw new RuntimeException("El título es obligatorio");
        }

        if (esTextoVacio(request.getContenido())) {
            throw new RuntimeException("El contenido es obligatorio");
        }

        if (request.getReacciones() != null && request.getReacciones() < 0) {
            throw new RuntimeException("Las reacciones no pueden ser negativas");
        }
    }

    private boolean esTextoVacio(String valor) {

        return valor == null || valor.trim().isEmpty();
    }

    private String normalizarTextoOpcional(String valor) {

        return esTextoVacio(valor) ? null : valor.trim();
    }

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
                .build();
    }
}
