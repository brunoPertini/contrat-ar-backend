package com.contractar.microserviciousuario.promotions;

import com.contractar.microservicioadapter.enums.PromotionType;
import com.contractar.microserviciousuario.repository.PromotionInstanceRepository;
import com.contractar.microserviciousuario.repository.PromotionRepository;

public class FullDiscountForeverEvaluator extends PromotionEvaluator {

	private static final int MAX_TO_BE_APPLIED = 10;
	private PromotionRepository promotionRepository;

	public FullDiscountForeverEvaluator(PromotionInstanceRepository repository,
			PromotionRepository promotionRepository) {
		super(repository);
		this.promotionRepository = promotionRepository;
	}

	@Override
	public boolean canPromotionBeApllied() {
		int currentCount = this.promotionInstanceRepository.countByPromotionType(PromotionType.FULL_DISCOUNT_FOREVER);
		return promotionRepository.findByType(PromotionType.FULL_DISCOUNT_FOREVER).isEnabled()
				&& (currentCount >= 0 && currentCount < MAX_TO_BE_APPLIED);
	}

}
