package com.StreamGo.controller;

import com.StreamGo.dao.SuscripcionDAO;
import com.StreamGo.entity.Suscripcion;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
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

    private final SuscripcionDAO suscripcionDAO;

    /**
     * Lista todas las suscripciones registradas.
     *
     * @return lista completa de suscripciones
     */
    @GetMapping("/todos")
    public ResponseEntity<List<Suscripcion>> listarTodas() {
        return ResponseEntity.ok(suscripcionDAO.findAll());
    }

    /**
     * Lista las suscripciones en estado ACTIVA.
     *
     * @return lista de suscripciones activas
     */
    @GetMapping("/activas")
    public ResponseEntity<List<Suscripcion>> activas() {
        return ResponseEntity.ok(
                suscripcionDAO.findAll()
                        .stream()
                        .filter(s -> s.getEstado().name().equals("ACTIVA"))
                        .collect(Collectors.toList())
        );
    }

    /**
     * Lista las suscripciones en estado VENCIDA.
     *
     * @return lista de suscripciones vencidas
     */
    @GetMapping("/vencidas")
    public ResponseEntity<List<Suscripcion>> vencidas() {
        return ResponseEntity.ok(
                suscripcionDAO.findAll()
                        .stream()
                        .filter(s -> s.getEstado().name().equals("VENCIDA"))
                        .collect(Collectors.toList())
        );
    }

    /**
     * Lista las suscripciones ordenadas por estado.
     *
     * @return lista de suscripciones ordenadas
     */
    @GetMapping("/ordenadas")
    public ResponseEntity<List<Suscripcion>> ordenadas() {

        List<Suscripcion> lista = suscripcionDAO.findAll();

        List<Suscripcion> ordenada = lista.stream()
                .sorted(Comparator.comparing(
                        (Suscripcion s) -> s.getEstado().name()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ordenada);
    }
}
