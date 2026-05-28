package com.StreamGo.service;

import com.StreamGo.entity.Plan;
import com.StreamGo.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;

    // Crear plan
    public Plan crearPlan(Plan plan) {

        plan.setActivo(true);
        plan.setId(null);
        return planRepository.save(plan);
    }

    // Obtener todos
    public List<Plan> listarPlanes() {

        return planRepository.findAll();
    }

    // Buscar por ID
    public Plan obtenerPlan(Long id) {

        return planRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Plan no encontrado"));
    }

    // Actualizar
    public Plan actualizarPlan(Long id, Plan nuevoPlan) {

        Plan plan = obtenerPlan(id);

        plan.setNombre(nuevoPlan.getNombre());
        plan.setPrecio(nuevoPlan.getPrecio());
        plan.setDuracionHoras(nuevoPlan.getDuracionHoras());
        plan.setDescripcion(nuevoPlan.getDescripcion());
        plan.setActivo(nuevoPlan.getActivo());

        return planRepository.save(plan);
    }

    // Eliminar lógico
    public void desactivarPlan(Long id) {

        Plan plan = obtenerPlan(id);

        plan.setActivo(false);

        planRepository.save(plan);
    }
}