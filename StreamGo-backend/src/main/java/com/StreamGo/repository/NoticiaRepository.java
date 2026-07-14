package com.StreamGo.repository;

import com.StreamGo.entity.Noticia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoticiaRepository extends JpaRepository<Noticia, Long> {

    @EntityGraph(attributePaths = {"autor", "usuario"})
    List<Noticia> findByAutorId(Long idAutor);

    @EntityGraph(attributePaths = {"autor", "usuario"})
    List<Noticia> findByUsuarioId(Long idUsuario);

    @EntityGraph(attributePaths = {"autor", "usuario"})
    List<Noticia> findAllByOrderByFijadoDescReaccionesDesc();

    @EntityGraph(attributePaths = {"autor", "usuario"})
    @Query("""
            SELECT n
            FROM Noticia n
            LEFT JOIN n.autor autor
            WHERE (:search IS NULL
                   OR LOWER(n.titulo) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(n.contenido) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(autor.nombre) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:fijado IS NULL OR n.fijado = :fijado)
            """)
    Page<Noticia> buscarNoticias(
            @Param("search") String search,
            @Param("fijado") Boolean fijado,
            Pageable pageable
    );
}
