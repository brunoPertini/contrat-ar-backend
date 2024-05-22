package com.contractar.microserviciousuario.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.contractar.microserviciousuario.models.Plan;
import com.contractar.microserviciousuario.repository.PlanRepository;

@Service
public class ProveedorService {
	
	private PlanRepository planRepository;
	
	public ProveedorService(PlanRepository planRepository) {
		this.planRepository = planRepository;
	}
	
	public Plan findPlanById(Long planId) {
		Optional<Plan> planOpt = planRepository.findById(planId);
		
		return planOpt.isPresent() ? planOpt.get() : null;
	}
	
	public List<Plan> findAll() {
		return planRepository.findAll();
	}
}
