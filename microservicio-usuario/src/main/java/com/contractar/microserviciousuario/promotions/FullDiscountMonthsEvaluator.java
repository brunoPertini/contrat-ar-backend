package com.contractar.microserviciousuario.promotions;

import com.contractar.microserviciousuario.repository.PromotionInstanceRepository;

public class FullDiscountMonthsEvaluator extends PromotionEvaluator {

	public FullDiscountMonthsEvaluator(PromotionInstanceRepository promotionInstanceRepository) {
		super(promotionInstanceRepository);
	}

	@Override
	public boolean canPromotionBeApllied() {
		return true;
	}

}
