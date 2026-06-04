package com.StreamGo.controller;


import com.StreamGo.dto.response.ClienteAdminResponse;
import com.StreamGo.service.AdminService;
import lombok.RequiredArgsConstructor;
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
}