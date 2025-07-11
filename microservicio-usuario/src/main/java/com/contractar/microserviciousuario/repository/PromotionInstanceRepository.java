package com.contractar.microserviciousuario.repository;

import org.springframework.data.repository.CrudRepository;

import com.contractar.microserviciousuario.models.PromotionInstance;
import com.contractar.microserviciousuario.models.PromotionInstanceId;
import com.contractar.microserviciousuario.models.PromotionType;

public interface PromotionInstanceRepository extends CrudRepository<PromotionInstance, PromotionInstanceId>{
	public int countByPromotionType(PromotionType type); 
}
