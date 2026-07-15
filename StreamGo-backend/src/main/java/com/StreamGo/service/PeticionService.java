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

/**
 * Servicio que gestiona las peticiones de usuarios y contenidos votables.
 * 
 * <p>Este servicio proporciona funcionalidades tanto para administradores como para clientes,
 * permitiendo la gestión de contenidos que pueden ser votados por los usuarios y el registro
 * de las peticiones/elecciones de películas.</p>
 * 
 * <h2>Funcionalidades principales:</h2>
 * <ul>
 *   <li><b>Administradores:</b> Crear, editar y desactivar contenidos votables, ver ranking de votos</li>
 *   <li><b>Clientes:</b> Ver contenidos disponibles, elegir/votar por una película</li>
 * </ul>
 * 
 * @author StreamGo Team
 * @version 1.0
 * @since 2026-05-28
 */
@Service
@RequiredArgsConstructor
public class PeticionService {

    private static final Logger log = LoggerFactory.getLogger(PeticionService.class);

    private final PeticionRepository peticionRepository;
    private final ContenidoVotableRepository contenidoVotableRepository;
    private final UsuarioRepository usuarioRepository;

    // ── ADMIN ──────────────────────────────────────────

    /**
     * Agrega un nuevo contenido votable al sistema.
     * 
     * <p>Este método crea un nuevo contenido (película/serie) que los usuarios podrán
     * votar o elegir posteriormente. El contenido se crea automáticamente como activo.</p>
     * 
     * @param request Objeto con los datos del contenido a crear (título, descripción, URLs de imágenes)
     * @return {@link ContenidoVotableResponse} con los datos del contenido creado, incluyendo su ID
     * @throws RuntimeException Si ocurre un error durante el guardado (loggeado internamente)
     */
    public ContenidoVotableResponse agregarVotable(ContenidoVotableRequest request) {
        log.debug("Agregando nuevo contenido votable: titulo={}", request.getTitulo());
        ContenidoVotable votable = ContenidoVotable.builder()
                .titulo(request.getTitulo())
                .descripcion(request.getDescripcion())
                .posterUrl(request.getPosterUrl())
                .imagenUrl(request.getImagenUrl())
                .cantidadVotos(0)
                .activo(true)
                .build();
        ContenidoVotableResponse response = mapVotableToResponse(contenidoVotableRepository.save(votable));
        log.info("Contenido votable creado exitosamente: id={}, titulo={}", response.getId(), response.getTitulo());
        return response;
    }

    /**
     * Edita un contenido votable existente.
     * 
     * <p>Permite modificar los campos de un contenido votable previamente creado.
     * Si el contenido está inactivo, se registra una advertencia pero se permite la edición.</p>
     * 
     * @param id ID del contenido votable a editar
     * @param request Objeto con los nuevos datos del contenido
     * @return {@link ContenidoVotableResponse} con los datos actualizados del contenido
     * @throws RuntimeException Si no se encuentra el contenido votable con el ID especificado
     */
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

    /**
     * Desactiva un contenido votable sin eliminarlo de la base de datos.
     * 
     * <p>El contenido permanece en el sistema pero no será visible para los clientes
     * ni podrá recibir nuevos votos. Si tiene votos asociados, se registra una advertencia.</p>
     * 
     * @param id ID del contenido votable a desactivar
     * @throws RuntimeException Si no se encuentra el contenido votable con el ID especificado
     */
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

    /**
     * Obtiene el ranking de todos los contenidos votables ordenados por cantidad de votos.
     * 
     * <p>El ranking muestra cada contenido votable con el total de votos recibidos,
     * ordenados de mayor a menor cantidad de votos.</p>
     * 
     * @return {@link List} de {@link VotoResponse} con el ID, título y total de votos de cada contenido
     */
    public List<VotoResponse> verRankingVotos() {
        log.debug("Consultando ranking de votos");

        List<VotoResponse> ranking = contenidoVotableRepository
                .findAllByOrderByCantidadVotosDesc()
                .stream()
                .map(c -> VotoResponse.builder()
                        .contenidoVotableId(c.getId())
                        .titulo(c.getTitulo())
                        .totalVotos(c.getCantidadVotos().longValue())
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

    /**
     * Lista todos los contenidos votables que están actualmente activos.
     * 
     * <p>Este método es utilizado por los clientes para ver qué películas o series
     * están disponibles para votar.</p>
     * 
     * @return {@link List} de {@link ContenidoVotableResponse} con los contenidos activos.
     *         Si no hay contenidos activos, retorna una lista vacía.
     */
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

    /**
     * Registra la elección/voto de un usuario por un contenido votable.
     * 
     * <p>Si el usuario ya había votado anteriormente, este método actualiza su elección
     * al nuevo contenido seleccionado (un usuario solo puede tener una petición activa).</p>
     * 
     * @param email Email del usuario que realiza la petición (identificador único)
     * @param request Objeto con el ID del contenido votable seleccionado
     * @return {@link PeticionResponse} con los detalles de la petición registrada
     * @throws RuntimeException Si el usuario no existe o el contenido votable no existe
     */
    public PeticionResponse elegirPelicula(String email, PeticionRequest request) {

        log.debug("Usuario {} intentando elegir contenido votable id={}", email, request.getContenidoVotableId());

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        ContenidoVotable nuevoContenido = contenidoVotableRepository
                .findById(request.getContenidoVotableId())
                .orElseThrow(() -> new RuntimeException("Contenido votable no encontrado"));

        if (!nuevoContenido.getActivo()) {
            throw new RuntimeException("El contenido votable está inactivo");
        }

        Optional<Peticion> existente = peticionRepository.findByUsuarioId(usuario.getId());

        Peticion peticion;

        if (existente.isPresent()) {

            peticion = existente.get();

            ContenidoVotable contenidoAnterior = peticion.getContenidoVotable();

            if (!contenidoAnterior.getId().equals(nuevoContenido.getId())) {

                contenidoAnterior.setCantidadVotos(
                        contenidoAnterior.getCantidadVotos() - 1);

                nuevoContenido.setCantidadVotos(
                        nuevoContenido.getCantidadVotos() + 1);

                contenidoVotableRepository.save(contenidoAnterior);
                contenidoVotableRepository.save(nuevoContenido);

                peticion.setContenidoVotable(nuevoContenido);
            }

            peticion.setFechaCreacion(LocalDateTime.now());

        } else {

            nuevoContenido.setCantidadVotos(
                    nuevoContenido.getCantidadVotos() + 1);

            contenidoVotableRepository.save(nuevoContenido);

            peticion = Peticion.builder()
                    .usuario(usuario)
                    .contenidoVotable(nuevoContenido)
                    .fechaCreacion(LocalDateTime.now())
                    .build();
        }

        PeticionResponse response = mapPeticionToResponse(
                peticionRepository.save(peticion));

        log.info("Petición guardada: usuarioId={}, contenidoVotableId={}, titulo={}",
                usuario.getId(), nuevoContenido.getId(), nuevoContenido.getTitulo());

        return response;
    }

    // ── ADMINISTRACIÓN DE PETICIONES (PARA CONTROLLERS) ──

    /**
     * Guarda una petición (para administración).
     * 
     * @param peticion Entidad Peticion a guardar
     * @return La petición guardada
     */
    public Peticion guardar(Peticion peticion) {
        log.debug("Guardando petición en administración: id={}", peticion.getId());
        return peticionRepository.save(peticion);
    }

    /**
     * Elimina una petición por su ID.
     * 
     * @param id ID de la petición a eliminar
     */
    public void eliminar(Long id) {
        log.info("Eliminando petición con ID: {}", id);
        peticionRepository.deleteById(id);
    }

    /**
     * Lista todas las peticiones (para administración).
     * 
     * @return Lista de todas las peticiones
     */
    public List<PeticionResponse> listarTodas() {
        log.debug("Listando todas las peticiones para administración");
        return peticionRepository.findAll()
                .stream()
                .map(this::mapPeticionToResponse)
                .toList();
    }

    /**
     * Obtiene una petición por su ID.
     * 
     * @param id ID de la petición
     * @return PeticionResponse con los datos de la petición
     * @throws RuntimeException si no se encuentra la petición
     */
    public PeticionResponse obtenerPorId(Long id) {
        log.debug("Obteniendo petición por ID: {}", id);
        Peticion peticion = peticionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Petición no encontrada con ID: {}", id);
                    return new RuntimeException("Petición no encontrada con ID: " + id);
                });
        return mapPeticionToResponse(peticion);
    }

    // ── MAPPERS ────────────────────────────────────────

    /**
     * Convierte una entidad {@link ContenidoVotable} a un DTO {@link ContenidoVotableResponse}.
     * 
     * @param v Entidad ContenidoVotable a convertir
     * @return DTO con los datos del contenido votable
     */
    private ContenidoVotableResponse mapVotableToResponse(ContenidoVotable v) {
    return ContenidoVotableResponse.builder()
            .id(v.getId())
            .titulo(v.getTitulo())
            .descripcion(v.getDescripcion())
            .posterUrl(v.getPosterUrl())
            .imagenUrl(v.getImagenUrl())
            .cantidadVotos(v.getCantidadVotos())
            .activo(v.getActivo())
            .build();
}

    /**
     * Convierte una entidad {@link Peticion} a un DTO {@link PeticionResponse}.
     * 
     * @param p Entidad Peticion a convertir
     * @return DTO con los datos de la petición, incluyendo información del usuario y contenido
     */
    private PeticionResponse mapPeticionToResponse(Peticion p) {
        return PeticionResponse.builder()
                .id(p.getId())
                .usuarioId(p.getUsuario().getId())
                .contenidoVotableId(p.getContenidoVotable().getId())
                .tituloPelicula(p.getContenidoVotable().getTitulo())
                .fechaCreacion(p.getFechaCreacion())
                .build();
    }
}
