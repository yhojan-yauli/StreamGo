package com.StreamGo.service;

import com.StreamGo.dto.response.ClienteAdminResponse;
import com.StreamGo.dao.UsuarioDAO;
import com.StreamGo.entity.Enum.Rol;
import com.StreamGo.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de administración del sistema StreamGo.
 * Gestiona a clientes y sus suscripciones.
 * @author Yhojan Yauli
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UsuarioDAO usuarioDAO;
    private final SuscripcionService suscripcionService;

    /**
     * Obtiene la lista de clientes registrados con información de suscripción.
     * Calcula si el cliente tiene suscripción activa y suma todas sus horas restantes.
     *
     * @return lista de clientes con datos administrativos.
     */
    public List<ClienteAdminResponse> obtenerClientes() {

        List<Usuario> clientes = usuarioDAO.findByRol(Rol.CLIENTE);

        return clientes.stream().map(usuario -> {

            boolean tieneSuscripcion =
                    suscripcionService.usuarioTieneSuscripcionActiva(usuario);

            long horasRestantes =
                    suscripcionService.calcularHorasRestantesTotales(usuario);

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
}
