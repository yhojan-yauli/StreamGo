package com.StreamGo.controller;

import com.StreamGo.entity.Plan;
import com.StreamGo.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador público de planes.
 * Permite consultar planes disponibles sin autenticación.
 *
 * @author Yhojan Yauli
 * @version 1.0
 */
@RestController
@RequestMapping("/planes")
@RequiredArgsConstructor
public class PlanPublicController {

    private final PlanService planService;

    /**
     * Lista todos los planes disponibles.
     *
     * @return lista de planes
     */
    @GetMapping
    public ResponseEntity<List<Plan>> listarPlanes() {
        return ResponseEntity.ok(planService.listarPlanes());
    }

    /**
     * Obtiene el detalle de un plan por su ID.
     *
     * @param id identificador del plan
     * @return plan encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<Plan> obtenerPlan(@PathVariable Long id) {
        return ResponseEntity.ok(planService.obtenerPlan(id));
    }
}