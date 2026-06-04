package com.StreamGo.repository;

import com.StreamGo.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    Optional<Plan> findByPrecioAndPersonalizadoTrue(Double precio);}