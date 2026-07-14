package com.StreamGo.controller;


import com.StreamGo.dto.response.ClienteAdminResponse;
import com.StreamGo.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador de administración del sistema StreamGo.
 * Permite gestionar  a  clientes.
 *
 * @author Yhojan Yauli
 * @version 1.0
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * Obtiene la lista de todos los clientes registrados.
     *
     * @return lista de clientes
     */
    @GetMapping("/clientes")
    public List<ClienteAdminResponse> obtenerClientes() {
        return adminService.obtenerClientes();
    }

    /**
     * Obtiene clientes paginados con información de suscripción.
     *
     * @param pageable parámetros de paginación (page, size, sort).
     * @return página de clientes.
     */
    @GetMapping("/clientes/paginados")
    public Page<ClienteAdminResponse> obtenerClientesPaginados(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return adminService.obtenerClientesPaginados(pageable);
    }
}
