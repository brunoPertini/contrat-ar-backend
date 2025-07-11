package com.contractar.microserviciousuario.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.contractar.microserviciousuario.models.Promotion;
import com.contractar.microserviciousuario.models.PromotionType;
import com.contractar.microserviciousuario.promotions.FullDiscountForeverEvaluator;
import com.contractar.microserviciousuario.promotions.FullDiscountMonthsEvaluator;
import com.contractar.microserviciousuario.promotions.PromotionEvaluator;
import com.contractar.microserviciousuario.repository.PromotionInstanceRepository;
import com.contractar.microserviciousuario.repository.PromotionRepository;

@Service
public class PromotionService {
	private PromotionRepository repository;

	private PromotionInstanceRepository promotionInstanceRepository;

	private final Map<PromotionType, PromotionEvaluator> evaluatorFactory;

	public PromotionService(PromotionRepository repository, PromotionInstanceRepository promotionInstanceRepository) {
		this.repository = repository;
		this.promotionInstanceRepository = promotionInstanceRepository;
		this.evaluatorFactory = Map.of(PromotionType.FULL_DISCOUNT_FOREVER,
				new FullDiscountForeverEvaluator(this.promotionInstanceRepository),
				PromotionType.FULL_DISCOUNT_MONTHS, new FullDiscountMonthsEvaluator(this.promotionInstanceRepository));
	}

	public List<Promotion> findAll() {
		return repository.findAll();
	}
	
	public Promotion findByType(PromotionType type) {
		return repository.findByType(type);
	}

	public boolean isPromotionApplicable(PromotionType promoType) {
		return Optional.ofNullable(evaluatorFactory.get(promoType)).map(PromotionEvaluator::canPromotionBeApllied)
				.orElse(false);
	}
	
	
}
