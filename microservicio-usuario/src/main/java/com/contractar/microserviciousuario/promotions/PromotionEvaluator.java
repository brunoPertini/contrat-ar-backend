package com.contractar.microserviciousuario.promotions;

import com.contractar.microserviciousuario.repository.PromotionInstanceRepository;

public abstract class PromotionEvaluator {
	protected PromotionInstanceRepository promotionInstanceRepository;
	
	protected PromotionEvaluator(PromotionInstanceRepository promotionInstanceRepository) {
		this.promotionInstanceRepository = promotionInstanceRepository;
	}
	
	public abstract boolean canPromotionBeApllied();
}
