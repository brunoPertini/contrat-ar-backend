package com.contractar.microserviciousuario.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.contractar.microserviciousuario.models.Promotion;
import com.contractar.microserviciousuario.repository.PromotionRepository;

@Service
public class PromotionService {
	private PromotionRepository repository;
	
	public PromotionService(PromotionRepository repository) {
		this.repository = repository;
	}
	
	public List<Promotion> findAll() {
		return repository.findAll();
	}
}
