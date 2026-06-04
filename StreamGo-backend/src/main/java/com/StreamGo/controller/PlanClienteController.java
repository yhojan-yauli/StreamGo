package com.StreamGo.controller;

import com.StreamGo.dto.response.PlanPersonalizadoResponse;
import com.StreamGo.entity.Plan;
import com.StreamGo.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador de planes para clientes.
 * Permite consultar planes disponibles y personalizados.
 *
 * @author Yhojan Yauli
 * @version 1.0
 */
@RestController
@RequestMapping("/cliente/planes")
@RequiredArgsConstructor
public class PlanClienteController {

    private final PlanService planService;

    /**
     * Lista los planes disponibles para clientes.
     *
     * @return lista de planes disponibles
     */
    @GetMapping
    public ResponseEntity<List<Plan>> listarPlanes() {
        return ResponseEntity.ok(planService.listarPlanes());
    }

    /**
     * Obtiene el detalle de un plan específico.
     *
     * @param id identificador del plan
     * @return plan encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<Plan> obtenerPlan(@PathVariable Long id) {
        return ResponseEntity.ok(planService.obtenerPlan(id));
    }

    /**
     * Obtiene un plan personalizado según el monto ingresado.
     *
     * @param monto cantidad seleccionada por el cliente
     * @return plan personalizado generado
     */
    @GetMapping("/personalizado")
    public ResponseEntity<Plan> obtenerPlanPersonalizado(
            @RequestParam Double monto
    ) {
        return ResponseEntity.ok(
                planService.obtenerPlanPersonalizado(monto)
        );
    }
}