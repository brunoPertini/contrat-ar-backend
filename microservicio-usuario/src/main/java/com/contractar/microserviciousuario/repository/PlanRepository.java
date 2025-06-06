package com.contractar.microserviciousuario.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.contractar.microservicioadapter.enums.PlanType;
import com.contractar.microserviciousuario.models.Plan;

public interface PlanRepository extends CrudRepository<Plan, Long> {
	public Optional<Plan> findById(Long id);
	
	public Optional<Plan> findByType(PlanType type);
	
	public List<Plan> findAll();
}
