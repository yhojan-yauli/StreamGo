package com.StreamGo.Plan;


import com.StreamGo.entity.Plan;
import com.StreamGo.repository.PlanRepository;
import com.StreamGo.service.PlanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - PlanService")
class PlanServiceTest {

    @Mock
    private PlanRepository planRepository;

    @InjectMocks
    private PlanService planService;

    private Plan planMock;

    @BeforeEach
    void setUp() {
        planMock = Plan.builder()
                .id(1L)
                .nombre("Plan Premium")
                .precio(java.math.BigDecimal.valueOf(12.0))
                .personalizado(true)
                .build();
    }

    @Nested
    @DisplayName("Planes Personalizados")
    class PersonalizadosTests {

        @Test
        @DisplayName("Debería retornar un plan personalizado si el monto es válido")
        void obtenerPlanPersonalizado_Exito() {
            when(planRepository.findByPrecioAndPersonalizadoTrue(12.0)).thenReturn(Optional.of(planMock));

            Plan resultado = planService.obtenerPlanPersonalizado(12.0);

            assertNotNull(resultado);
            assertEquals("Plan Premium", resultado.getNombre());
        }

        @Test
        @DisplayName("Debería lanzar excepción si el monto está fuera del rango (S/3 - S/15)")
        void obtenerPlanPersonalizado_MontoInvalido() {
            assertThrows(RuntimeException.class, () -> planService.obtenerPlanPersonalizado(2.0));
            assertThrows(RuntimeException.class, () -> planService.obtenerPlanPersonalizado(20.0));
        }
    }

    @Test
    @DisplayName("Debería retornar un plan personalizado si el monto es válido")
    void obtenerPlanPersonalizado_Exito() {
        // Si tu servicio acepta Double en el método pero el repositorio usa BigDecimal internamente:
        when(planRepository.findByPrecioAndPersonalizadoTrue(12.0))
                .thenReturn(Optional.of(planMock));

        Plan resultado = planService.obtenerPlanPersonalizado(12.0);

        assertNotNull(resultado);
        assertEquals("Plan Premium", resultado.getNombre());
    }
}
