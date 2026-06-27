package com.StreamGo.dao;

import com.StreamGo.entity.Contenido;
import com.StreamGo.entity.Enum.EstadoContenido;

import java.util.List;

public interface ContenidoDAO extends IGenericDAO<Contenido, Long> {

    List<Contenido> findByEstado(EstadoContenido estado);

    List<Contenido> findByCategoriaAndEstado(
            String categoria,
            EstadoContenido estado
    );

    List<Contenido> findByRecomendadoTrueAndEstado(
            EstadoContenido estado
    );

    List<Contenido> findByTendenciaTrueAndEstado(
            EstadoContenido estado
    );

    List<Contenido> findByTituloContainingIgnoreCaseAndEstado(
            String titulo,
            EstadoContenido estado
    );
}
