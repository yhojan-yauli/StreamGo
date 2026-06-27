package com.StreamGo.dao;

import com.StreamGo.entity.Noticia;

import java.util.List;

public interface NoticiaDAO extends IGenericDAO<Noticia, Long> {

    List<Noticia> findByAutorId(Long idAutor);

    List<Noticia> findByUsuarioId(Long idUsuario);

    List<Noticia> findAllByOrderByFijadoDescReaccionesDesc();
}
