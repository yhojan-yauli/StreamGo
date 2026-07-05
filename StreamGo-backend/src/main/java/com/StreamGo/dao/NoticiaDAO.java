package com.StreamGo.dao;

import com.StreamGo.dto.query.NoticiaQuery;
import com.StreamGo.dto.response.PageResponse;
import com.StreamGo.entity.Noticia;

import java.util.List;

public interface NoticiaDAO extends IGenericDAO<Noticia, Long> {

    PageResponse<Noticia> findAll(NoticiaQuery query);

    PageResponse<Noticia> findAdminAll(NoticiaQuery query);

    long count(NoticiaQuery query);

    List<Noticia> findByAutorId(Long idAutor);

    List<Noticia> findByUsuarioId(Long idUsuario);

    List<Noticia> findAllByOrderByFijadoDescReaccionesDesc();
}
