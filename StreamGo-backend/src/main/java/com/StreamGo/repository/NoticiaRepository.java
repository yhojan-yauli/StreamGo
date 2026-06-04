package com.StreamGo.repository;

import com.StreamGo.entity.Noticia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticiaRepository extends JpaRepository<Noticia, Long> {

    List<Noticia> findByAutorId(Long idAutor);

    List<Noticia> findByUsuarioId(Long idUsuario);

    List<Noticia> findAllByOrderByFijadoDescReaccionesDesc();
}
