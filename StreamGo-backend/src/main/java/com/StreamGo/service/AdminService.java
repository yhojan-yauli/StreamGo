package com.StreamGo.service;

import com.StreamGo.dto.response.ClienteAdminResponse;
import com.StreamGo.entity.Enum.EstadoSuscripcion;
import com.StreamGo.entity.Enum.Rol;
import com.StreamGo.entity.Suscripcion;
import com.StreamGo.entity.Usuario;
import com.StreamGo.repository.SuscripcionRepository;
import com.StreamGo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio de administración del sistema StreamGo.
 * Gestiona a clientes y sus suscripciones.
 * @author Yhojan Yauli
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UsuarioRepository usuarioRepository;
    private final SuscripcionRepository suscripcionRepository;

    /**
     * Obtiene la lista de clientes registrados con información de suscripción.
     * Usa batch queries: 2 queries en vez de 2N+1.
     *
     * @return lista de clientes con datos administrativos.
     */
    public List<ClienteAdminResponse> obtenerClientes() {

        List<Usuario> clientes = usuarioRepository.findByRol(Rol.CLIENTE);

        if (clientes.isEmpty()) {
            return List.of();
        }

        List<Long> ids = clientes.stream().map(Usuario::getId).toList();

        Map<Long, Suscripcion> suscripcionesMap = obtenerSuscripcionesActivasMap(ids);

        LocalDateTime ahora = LocalDateTime.now();

        return clientes.stream().map(usuario -> construirClienteResponse(
                usuario, suscripcionesMap.get(usuario.getId()), ahora
        )).toList();
    }

    /**
     * Obtiene clientes paginados con información de suscripción.
     * Usa batch queries: 2 queries en vez de 2N+1.
     *
     * @param pageable parámetros de paginación.
     * @return página de clientes con datos administrativos.
     */
    public Page<ClienteAdminResponse> obtenerClientesPaginados(Pageable pageable) {

        Page<Usuario> clientes = usuarioRepository.findByRol(Rol.CLIENTE, pageable);

        if (clientes.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Long> ids = clientes.getContent().stream().map(Usuario::getId).toList();

        Map<Long, Suscripcion> suscripcionesMap = obtenerSuscripcionesActivasMap(ids);

        LocalDateTime ahora = LocalDateTime.now();

        return clientes.map(usuario -> construirClienteResponse(
                usuario, suscripcionesMap.get(usuario.getId()), ahora
        ));
    }

    /**
     * Trae todas las suscripciones activas de una lista de usuarios en 1 sola query.
     * Retorna un Map<usuarioId, Suscripcion> con la fecha fin más lejana por usuario.
     */
    private Map<Long, Suscripcion> obtenerSuscripcionesActivasMap(List<Long> usuarioIds) {

        List<Suscripcion> todas = suscripcionRepository
                .findByUsuarioIdInAndEstado(usuarioIds, EstadoSuscripcion.ACTIVA);

        LocalDateTime ahora = LocalDateTime.now();

        return todas.stream()
                .filter(s -> s.getFechaFin() != null && s.getFechaFin().isAfter(ahora))
                .collect(Collectors.toMap(
                        s -> s.getUsuario().getId(),
                        s -> s,
                        (a, b) -> a.getFechaFin().isAfter(b.getFechaFin()) ? a : b
                ));
    }

    /**
     * Construye la respuesta de un cliente con su información de suscripción.
     */
    private ClienteAdminResponse construirClienteResponse(
            Usuario usuario,
            Suscripcion suscripcionActiva,
            LocalDateTime ahora
    ) {
        boolean tieneSuscripcion = suscripcionActiva != null;
        long horasRestantes = 0;

        if (tieneSuscripcion) {
            long segundos = Duration.between(ahora, suscripcionActiva.getFechaFin()).getSeconds();
            horasRestantes = (segundos + 3599) / 3600;
        }

        return ClienteAdminResponse.builder()
                .avatar(usuario.getAvatar())
                .nombre(usuario.getNombre())
                .estado(usuario.getEstado().name())
                .email(usuario.getEmail())
                .telefono(usuario.getTelefono())
                .ultimoAcceso(
                        usuario.getUltimoAcceso() != null
                                ? usuario.getUltimoAcceso().toString()
                                : null
                )
                .tieneSuscripcion(tieneSuscripcion)
                .horasRestantes(horasRestantes)
                .build();
    }
}
