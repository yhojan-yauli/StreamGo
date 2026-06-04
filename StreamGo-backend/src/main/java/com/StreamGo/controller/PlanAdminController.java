package com.StreamGo.controller;

import com.StreamGo.entity.Plan;
import com.StreamGo.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Controlador administrativo de planes.
 * Permite gestionar CRUD de planes del sistema StreamGo.
 *
 * @author Yhojan Yauli
 * @version 1.0
 */
@RestController
@RequestMapping("/admin/planes")
@RequiredArgsConstructor
public class PlanAdminController {

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
     * Obtiene un plan por su ID.
     *
     * @param id identificador del plan
     * @return plan encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<Plan> obtenerPlan(@PathVariable Long id) {
        return ResponseEntity.ok(planService.obtenerPlan(id));
    }

    /**
     * Crea un nuevo plan.
     *
     * @param plan datos del plan a registrar
     * @return plan creado
     */
    @PostMapping
    public ResponseEntity<Plan> crearPlan(@RequestBody Plan plan) {
        return ResponseEntity.ok(planService.crearPlan(plan));
    }

    /**
     * Actualiza un plan existente.
     *
     * @param id identificador del plan
     * @param plan datos actualizados del plan
     * @return plan actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<Plan> actualizarPlan(
            @PathVariable Long id,
            @RequestBody Plan plan
    ) {
        return ResponseEntity.ok(planService.actualizarPlan(id, plan));
    }

    /**
     * Elimina un plan por su ID.
     *
     * @param id identificador del plan
     * @return mensaje de confirmación
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarPlan(@PathVariable Long id) {

        planService.eliminarPlan(id);

        return ResponseEntity.ok("Plan eliminado correctamente");
    }
}