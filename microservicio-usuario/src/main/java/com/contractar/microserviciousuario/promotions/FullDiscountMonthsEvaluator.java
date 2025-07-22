package com.contractar.microserviciousuario.promotions;

public class FullDiscountMonthsEvaluator extends PromotionEvaluator {
	
	private FullDiscountForeverEvaluator foreverEvaluator;

	public FullDiscountMonthsEvaluator(FullDiscountForeverEvaluator foreverEvaluator) {
		super();
		this.foreverEvaluator = foreverEvaluator;
	}

	@Override
	public boolean canPromotionBeApllied() {
		return !foreverEvaluator.canPromotionBeApllied();
	}

}
