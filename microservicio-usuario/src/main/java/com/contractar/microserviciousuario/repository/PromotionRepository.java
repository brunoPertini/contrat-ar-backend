package com.contractar.microserviciousuario.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.contractar.microserviciousuario.models.Promotion;
import com.contractar.microserviciousuario.models.PromotionType;

public interface PromotionRepository extends CrudRepository<Promotion, Long> {
	public List<Promotion> findAll();
	
	public Optional<Promotion> findById(Long id);
	
	public Promotion findByType(PromotionType type);
}