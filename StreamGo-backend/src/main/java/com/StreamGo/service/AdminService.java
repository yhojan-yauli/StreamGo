package com.StreamGo.service;

import com.StreamGo.dto.response.ClienteAdminResponse;
import com.StreamGo.entity.Enum.Rol;
import com.StreamGo.entity.Usuario;
import com.StreamGo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    private final SuscripcionService suscripcionService;

    /**
     * Obtiene la lista de clientes registrados con información de suscripción.
     * Calcula si el cliente tiene suscripción activa y suma todas sus horas restantes.
     *
     * @return lista de clientes con datos administrativos.
     */
    public List<ClienteAdminResponse> obtenerClientes() {

        List<Usuario> clientes = usuarioRepository.findByRol(Rol.CLIENTE);

        return clientes.stream().map(usuario -> {

            boolean tieneSuscripcion =
                    suscripcionService.tieneSuscripcionActivaSoloLectura(usuario);

            long horasRestantes =
                    suscripcionService.calcularHorasRestantesSoloLectura(usuario);

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

        }).toList();
    }

    /**
     * Obtiene clientes paginados con información de suscripción.
     *
     * @param pageable parámetros de paginación.
     * @return página de clientes con datos administrativos.
     */
    public Page<ClienteAdminResponse> obtenerClientesPaginados(Pageable pageable) {

        Page<Usuario> clientes = usuarioRepository.findByRol(Rol.CLIENTE, pageable);

        return clientes.map(usuario -> {

            boolean tieneSuscripcion =
                    suscripcionService.tieneSuscripcionActivaSoloLectura(usuario);

            long horasRestantes =
                    suscripcionService.calcularHorasRestantesSoloLectura(usuario);

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

        });
    }
}
