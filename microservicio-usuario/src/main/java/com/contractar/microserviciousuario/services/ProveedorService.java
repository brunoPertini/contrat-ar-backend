package com.contractar.microserviciousuario.services;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciousuario.models.Plan;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.repository.PlanRepository;
import com.contractar.microserviciousuario.repository.ProveedorRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class ProveedorService {
	
	private PlanRepository planRepository;
	
	private ProveedorRepository proveedorRepository;
		
	public ProveedorService(PlanRepository planRepository, ProveedorRepository proveedorRepository) {
		this.planRepository = planRepository;
		this.proveedorRepository = proveedorRepository;
	}
	
	public Proveedor findById(Long proveedorId) throws UserNotFoundException {		
		
		return proveedorRepository.findById(proveedorId).map(proveedor -> proveedor).orElseThrow(UserNotFoundException::new);
	}
	
	public List<Plan> findAll() {
		return planRepository.findAll();
	}
}
