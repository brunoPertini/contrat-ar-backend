package com.contractar.microserviciousuario.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.contractar.microserviciousuario.models.Promotion;

public interface PromotionRepository extends CrudRepository<Promotion, Long> {
	public List<Promotion> findAll();
}
