package com.StreamGo.service;

import com.StreamGo.dto.response.ClienteAdminResponse;
import com.StreamGo.entity.Suscripcion;
import com.StreamGo.entity.Usuario;
import com.StreamGo.entity.Enum.Rol;
import com.StreamGo.repository.SuscripcionRepository;
import com.StreamGo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de administración del sistema StreamGo.
 * Gestiona a clientes y sus suscripciones.
 *
 * @author Yhojan Yauli
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UsuarioRepository usuarioRepository;
    private final SuscripcionRepository suscripcionRepository;

    /**
     * Obtiene la lista de clientes registrados con información de suscripción.
     * Calcula si el cliente tiene suscripción activa y las horas restantes.
     *
     * @return lista de clientes con datos administrativos
     */
    public List<ClienteAdminResponse> obtenerClientes() {

        List<Usuario> clientes = usuarioRepository.findByRol(Rol.CLIENTE);

        return clientes.stream().map(usuario -> {

            // Buscar suscripción activa
            Suscripcion suscripcion = suscripcionRepository
                    .findByUsuario(usuario)
                    .orElse(null);

            boolean tieneSuscripcion = suscripcion != null;

            long horasRestantes = 0;

            // Calcular horas restantes de suscripción
            if (tieneSuscripcion) {

                horasRestantes = Duration.between(
                        LocalDateTime.now(),
                        suscripcion.getFechaFin()
                ).toHours();

                if (horasRestantes < 0) {
                    horasRestantes = 0;
                }
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

        }).toList();
    }
}