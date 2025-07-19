package com.contractar.microserviciousuario.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciocommons.dto.SuscripcionDTO;
import com.contractar.microserviciocommons.dto.usuario.PromotionInstanceCreate;
import com.contractar.microserviciocommons.exceptions.CantCreatePromotion;
import com.contractar.microserviciousuario.models.Promotion;
import com.contractar.microserviciousuario.models.PromotionInstance;
import com.contractar.microserviciousuario.models.PromotionInstanceId;
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
		
	private RestTemplate httpClient;
	
	@Value("${microservicio-config.url}")
	private String configServiceUrl;
	
	private final Map<PromotionType, PromotionEvaluator> evaluatorFactory;
	
	private SuscriptionService suscriptionService;

	public PromotionService(PromotionRepository repository, PromotionInstanceRepository promotionInstanceRepository,
			SuscriptionService suscriptionService,
			 RestTemplate httpClient) {
		this.repository = repository;
		this.promotionInstanceRepository = promotionInstanceRepository;
		this.suscriptionService = suscriptionService;
		
		this.evaluatorFactory = Map.of(PromotionType.FULL_DISCOUNT_FOREVER,
				new FullDiscountForeverEvaluator(this.promotionInstanceRepository),
				PromotionType.FULL_DISCOUNT_MONTHS, new FullDiscountMonthsEvaluator(this.promotionInstanceRepository));
		
		this.httpClient = httpClient;
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
	
	public String getMessageTag(String tagId) {
		final String fullUrl = configServiceUrl + "/i18n/" + tagId;
		return httpClient.getForObject(fullUrl, String.class);
	}
	
	public PromotionInstance createPromotionInstance(PromotionInstanceCreate dto) throws CantCreatePromotion {
		SuscripcionDTO suscription = suscriptionService.getSuscripcionById(dto.getSuscriptionId());
		
		if (!suscription.isActive() || !suscription.getValidity().isValid()) {
			throw new CantCreatePromotion(getMessageTag("exceptions.promotions.cantCreate"));
		}
		
		boolean alreadyHasLinkedPromotion = promotionInstanceRepository.findByIdPromotionIdAndIdSuscriptionIdAndExpirationDateAfter(dto.getPromotionId(),
				dto.getSuscriptionId(),
				LocalDate.now()).isPresent();
		
		if (alreadyHasLinkedPromotion) {
			throw new CantCreatePromotion(getMessageTag("exceptions.promotions.cantCreate"));
		}
		
		Promotion linkedPromotion = repository.findById(dto.getPromotionId()).map(p -> p).orElseThrow(() -> new CantCreatePromotion("exceptions.promotions.cantCreate"));
		
		LocalDate expirationDate = LocalDate.now().plusMonths(linkedPromotion.getExpirationMonths());
		
		PromotionInstance promotionInstance = new PromotionInstance(new PromotionInstanceId(dto.getSuscriptionId(), dto.getPromotionId()), expirationDate);
		promotionInstance.setPromotion(linkedPromotion);
		
		return promotionInstanceRepository.save(promotionInstance);
	}
	
}
