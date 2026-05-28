package com.StreamGo.controller;


import com.StreamGo.dto.response.ClienteAdminResponse;
import com.StreamGo.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // Obtener todos los clientes
    @GetMapping("/clientes")
    public List<ClienteAdminResponse> obtenerClientes() {

        return adminService.obtenerClientes();
    }
}