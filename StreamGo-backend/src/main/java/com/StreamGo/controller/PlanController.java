package com.StreamGo.controller;

import com.StreamGo.entity.Plan;
import com.StreamGo.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/planes")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    // Crear plan
    @PostMapping
    public ResponseEntity<Plan> crearPlan(
            @RequestBody Plan plan
    ) {

        return ResponseEntity.ok(
                planService.crearPlan(plan)
        );
    }

    // Listar planes
    @GetMapping
    public ResponseEntity<List<Plan>> listarPlanes() {

        return ResponseEntity.ok(
                planService.listarPlanes()
        );
    }

    // Obtener por ID
    @GetMapping("/{id}")
    public ResponseEntity<Plan> obtenerPlan(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                planService.obtenerPlan(id)
        );
    }

    // Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<Plan> actualizarPlan(
            @PathVariable Long id,
            @RequestBody Plan plan
    ) {

        return ResponseEntity.ok(
                planService.actualizarPlan(id, plan)
        );
    }

    // Desactivar
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarPlan(
            @PathVariable Long id
    ) {

        planService.desactivarPlan(id);

        return ResponseEntity.ok("Plan desactivado");
    }
}
