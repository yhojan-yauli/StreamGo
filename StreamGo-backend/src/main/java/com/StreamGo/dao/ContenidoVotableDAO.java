package com.StreamGo.dao;

import com.StreamGo.entity.ContenidoVotable;

import java.util.List;

public interface ContenidoVotableDAO
        extends IGenericDAO<ContenidoVotable, Long> {

    List<ContenidoVotable> findByActivoTrue();
}
