package com.StreamGo.dao;

import com.StreamGo.entity.Plan;

import java.util.Optional;

public interface PlanDAO extends IGenericDAO<Plan, Long> {

    Optional<Plan> findByPrecioAndPersonalizadoTrue(Double precio);
}
