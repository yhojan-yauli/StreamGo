package com.StreamGo.service;

import com.StreamGo.dao.PlanDAO;
import com.StreamGo.entity.Plan;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de gestión de planes del sistema StreamGo.
 * Permite crear, consultar, actualizar, eliminar y obtener planes personalizados.
 *
 * @author Yhojan Yauli
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class PlanService {

    private static final Logger log =
            LoggerFactory.getLogger(PlanService.class);

    private final PlanDAO planDAO;

    /**
     * Crea un nuevo plan en el sistema.
     *
     * @param plan datos del plan a registrar
     * @return plan creado y guardado
     */
    public Plan crearPlan(Plan plan) {

        log.debug("Creando nuevo plan: {}", plan.getNombre());

        plan.setActivo(true);
        plan.setId(null);

        planDAO.save(plan);

        log.info("Plan creado con ID: {}", plan.getId());

        return plan;
    }

    /**
     * Lista todos los planes registrados.
     *
     * @return lista de planes
     */
    public List<Plan> listarPlanes() {

        log.debug("Listando todos los planes");

        return planDAO.findAll();
    }

    /**
     * Obtiene un plan por su ID.
     *
     * @param id identificador del plan
     * @return plan encontrado
     */
    public Plan obtenerPlan(Long id) {

        log.debug("Buscando plan con ID: {}", id);

        Plan plan = planDAO.findById(id);

        log.info("Plan encontrado: {}", plan.getNombre());

        return plan;
    }

    /**
     * Actualiza los datos de un plan existente.
     *
     * @param id identificador del plan
     * @param nuevoPlan datos actualizados
     * @return plan actualizado
     */
    public Plan actualizarPlan(Long id, Plan nuevoPlan) {

        log.debug("Actualizando plan ID: {}", id);

        Plan plan = obtenerPlan(id);

        plan.setNombre(nuevoPlan.getNombre());
        plan.setPrecio(nuevoPlan.getPrecio());
        plan.setDuracionHoras(nuevoPlan.getDuracionHoras());
        plan.setDescripcion(nuevoPlan.getDescripcion());
        plan.setActivo(nuevoPlan.getActivo());

        planDAO.update(plan);

        log.info("Plan actualizado ID: {}", id);

        return plan;
    }

    /**
     * Elimina un plan por su ID.
     *
     * @param id identificador del plan
     */
    public void eliminarPlan(Long id) {

        log.warn("Eliminando plan ID: {}", id);

        obtenerPlan(id);
        planDAO.delete(id);

        log.info("Plan eliminado correctamente ID: {}", id);
    }

    /**
     * Obtiene un plan personalizado según el monto ingresado.
     *
     * @param monto valor del plan solicitado
     * @return plan personalizado correspondiente
     */
    public Plan obtenerPlanPersonalizado(Double monto) {

        log.debug("Buscando plan personalizado con monto: {}", monto);

        if (monto < 3 || monto > 15) {

            log.warn("Monto inválido: {}", monto);

            throw new RuntimeException(
                    "El monto debe estar entre S/3 y S/15"
            );
        }

        Plan plan = planDAO
                .findByPrecioAndPersonalizadoTrue(monto)
                .orElseThrow(() -> {

                    log.error("No existe plan personalizado para: {}", monto);

                    return new RuntimeException(
                            "No existe plan personalizado para ese monto"
                    );
                });

        log.info("Plan personalizado encontrado: {}", plan.getNombre());

        return plan;
    }
}
