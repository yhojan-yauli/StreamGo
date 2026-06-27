package com.StreamGo.dao;

import com.StreamGo.entity.Peticion;

import java.util.List;
import java.util.Optional;

public interface PeticionDAO extends IGenericDAO<Peticion, Long> {

    Optional<Peticion> findByUsuarioId(Long usuarioId);

    List<Object[]> contarVotosPorContenido();

    long countByContenidoVotableId(Long contenidoVotableId);
}
