package com.StreamGo.repository;

import com.StreamGo.entity.ContenidoVotable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContenidoVotableRepository extends JpaRepository<ContenidoVotable, Long> {

    List<ContenidoVotable> findByActivoTrue();
    
    List<ContenidoVotable> findAllByOrderByCantidadVotosDesc();
}
