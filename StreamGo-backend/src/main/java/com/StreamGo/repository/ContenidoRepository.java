package com.StreamGo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.StreamGo.entity.Contenido;
import com.StreamGo.entity.Enum.EstadoContenido;

public interface ContenidoRepository extends JpaRepository<Contenido, Long> {

    List<Contenido> findByEstado(EstadoContenido estado);

    List<Contenido> findByCategoriaAndEstado(String categoria, EstadoContenido estado);

    List<Contenido> findByRecomendadoTrueAndEstado(EstadoContenido estado);

    List<Contenido> findByTendenciaTrueAndEstado(EstadoContenido estado);

    List<Contenido> findByTituloContainingIgnoreCaseAndEstado(String titulo, EstadoContenido estado);
}