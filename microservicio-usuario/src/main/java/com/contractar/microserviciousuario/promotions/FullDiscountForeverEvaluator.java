package com.contractar.microserviciousuario.promotions;

import com.contractar.microservicioadapter.enums.PromotionType;
import com.contractar.microserviciousuario.repository.PromotionInstanceRepository;

public class FullDiscountForeverEvaluator extends PromotionEvaluator {

	private static final int MAX_TO_BE_APPLIED = 10;

	public FullDiscountForeverEvaluator(PromotionInstanceRepository repository) {
		super(repository);
	}

	@Override
	public boolean canPromotionBeApllied() {
		int currentCount = this.promotionInstanceRepository
				.countByPromotionType(PromotionType.FULL_DISCOUNT_FOREVER);
		return currentCount >= 0 && currentCount < MAX_TO_BE_APPLIED;
	}

}
