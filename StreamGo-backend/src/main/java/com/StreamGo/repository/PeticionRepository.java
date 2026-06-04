package com.StreamGo.repository;

import com.StreamGo.entity.Peticion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface PeticionRepository extends JpaRepository<Peticion, Long> {

    Optional<Peticion> findByUsuarioId(Long usuarioId);

    @Query("SELECT p.contenidoVotable.id, p.contenidoVotable.titulo, COUNT(p) " +
           "FROM Peticion p GROUP BY p.contenidoVotable.id, p.contenidoVotable.titulo " +
           "ORDER BY COUNT(p) DESC")
    List<Object[]> contarVotosPorContenido();
    
        long countByContenidoVotableId(Long contenidoVotableId);
}