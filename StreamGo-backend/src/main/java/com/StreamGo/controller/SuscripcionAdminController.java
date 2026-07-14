package com.StreamGo.controller;

import com.StreamGo.entity.Enum.EstadoSuscripcion;
import com.StreamGo.entity.Suscripcion;
import com.StreamGo.repository.SuscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador administrativo de suscripciones.
 * Permite consultar, filtrar y ordenar suscripciones del sistema.
 *
 * @author Yhojan Yauli
 * @version 1.0
 */
@RestController
@RequestMapping("/admin/suscripciones")
@RequiredArgsConstructor
public class SuscripcionAdminController {

    private final SuscripcionRepository suscripcionRepository;

    /**
     * Lista todas las suscripciones registradas (paginadas).
     *
     * @param pageable parámetros de paginación.
     * @return página de suscripciones.
     */
    @GetMapping("/todos")
    public ResponseEntity<Page<Suscripcion>> listarTodas(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(suscripcionRepository.findAll(pageable));
    }

    /**
     * Lista las suscripciones en estado ACTIVA (paginadas).
     *
     * @param pageable parámetros de paginación.
     * @return página de suscripciones activas.
     */
    @GetMapping("/activas")
    public ResponseEntity<Page<Suscripcion>> activas(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(
                suscripcionRepository.findByEstado(EstadoSuscripcion.ACTIVA, pageable)
        );
    }

    /**
     * Lista las suscripciones en estado VENCIDA (paginadas).
     *
     * @param pageable parámetros de paginación.
     * @return página de suscripciones vencidas.
     */
    @GetMapping("/vencidas")
    public ResponseEntity<Page<Suscripcion>> vencidas(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(
                suscripcionRepository.findByEstado(EstadoSuscripcion.VENCIDA, pageable)
        );
    }

    /**
     * Lista las suscripciones ordenadas por estado (paginadas).
     *
     * @param pageable parámetros de paginación.
     * @return página de suscripciones ordenadas.
     */
    @GetMapping("/ordenadas")
    public ResponseEntity<Page<Suscripcion>> ordenadas(
            @PageableDefault(size = 10, sort = "estado") Pageable pageable
    ) {
        return ResponseEntity.ok(suscripcionRepository.findAll(pageable));
    }
}
