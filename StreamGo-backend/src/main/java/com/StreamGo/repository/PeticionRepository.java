package com.StreamGo.repository;

import com.StreamGo.entity.Peticion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PeticionRepository
        extends JpaRepository<Peticion, Long> {

}