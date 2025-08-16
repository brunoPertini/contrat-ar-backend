package com.contractar.microserviciousuario.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.contractar.microservicioadapter.enums.PlanType;
import com.contractar.microservicioadapter.enums.PromotionType;
import com.contractar.microserviciocommons.dto.SuscripcionDTO;
import com.contractar.microserviciocommons.dto.UserPromotionDTO;
import com.contractar.microserviciocommons.dto.usuario.PromotionInstanceCreate;
import com.contractar.microserviciocommons.dto.usuario.PromotionInstanceDTO;
import com.contractar.microserviciocommons.exceptions.CantCreatePromotion;
import com.contractar.microserviciocommons.exceptions.proveedores.SuscriptionNotFound;
import com.contractar.microserviciousuario.models.Promotion;
import com.contractar.microserviciousuario.models.PromotionInstance;
import com.contractar.microserviciousuario.models.PromotionInstanceId;
import com.contractar.microserviciousuario.promotions.FullDiscountForeverEvaluator;
import com.contractar.microserviciousuario.promotions.FullDiscountMonthsEvaluator;
import com.contractar.microserviciousuario.promotions.PromotionEvaluator;
import com.contractar.microserviciousuario.repository.PlanRepository;
import com.contractar.microserviciousuario.repository.PromotionInstanceRepository;
import com.contractar.microserviciousuario.repository.PromotionRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PromotionService {
	private PromotionRepository repository;

	private PromotionInstanceRepository promotionInstanceRepository;

	private RestTemplate httpClient;

	@Value("${microservicio-config.url}")
	private String configServiceUrl;

	private final Map<PromotionType, PromotionEvaluator> evaluatorFactory;

	private SuscriptionService suscriptionService;

	private PlanRepository planRepository;

	public PromotionService(PromotionRepository repository, PromotionInstanceRepository promotionInstanceRepository,
			SuscriptionService suscriptionService, PlanRepository planRepository, RestTemplate httpClient) {
		this.repository = repository;
		this.promotionInstanceRepository = promotionInstanceRepository;
		this.planRepository = planRepository;

		this.suscriptionService = suscriptionService;

		FullDiscountForeverEvaluator foreverEvaluator = new FullDiscountForeverEvaluator(
				this.promotionInstanceRepository, repository);

		this.evaluatorFactory = Map.of(PromotionType.FULL_DISCOUNT_FOREVER, foreverEvaluator,
				PromotionType.FULL_DISCOUNT_MONTHS, new FullDiscountMonthsEvaluator(foreverEvaluator));

		this.httpClient = httpClient;
	}

	public UserPromotionDTO findUserPromotion(Long suscriptionId) {
		return promotionInstanceRepository.findByIdSuscriptionIdAndExpirationDateAfter(suscriptionId, LocalDate.now())
				.map(instance -> new UserPromotionDTO(instance.getPromotion().getText(), instance.getExpirationDate(),
						instance.getPromotion().getType(), instance.getPromotion().getId()))
				.orElse(null);
	}

	public List<Promotion> findAll() {
		return repository.findAll().stream().filter(p -> isPromotionApplicable(p.getType())).toList();
	}

	public List<Promotion> findAllAplicable(Long userId) {
		// First I get the applicable by the system, then I check if each one is
		// applicable for current subscription
		List<Promotion> bySystemApplicable = findAll();

		return bySystemApplicable.stream().filter(promotion -> promotionInstanceRepository
				.findByPromotionIdAndProveedorId(promotion.getId(), userId).isEmpty()).toList();

	}

	public Promotion findByType(PromotionType type) {
		return repository.findByType(type);
	}

	public Optional<Promotion> findById(Long id) {
		return repository.findById(id);
	}

	public boolean isPromotionApplicable(PromotionType promoType) {
		return repository.findByType(promoType).isEnabled() && Optional.ofNullable(evaluatorFactory.get(promoType))
				.map(PromotionEvaluator::canPromotionBeApllied).orElse(false);
	}

	public String getMessageTag(String tagId) {
		final String fullUrl = configServiceUrl + "/i18n/" + tagId;
		return httpClient.getForObject(fullUrl, String.class);
	}

	public Promotion findCurrentApplicable() {
		return repository.findAll().stream().filter(promotion -> {
			return isPromotionApplicable(promotion.getType());
		}).findFirst().map(p -> p).orElse(null);
	}

	public PromotionInstance createPromotionInstance(PromotionInstanceCreate dto)
			throws CantCreatePromotion, SuscriptionNotFound {
		SuscripcionDTO suscription = suscriptionService.getSuscripcionById(dto.getSuscriptionId(), false);

		boolean isApplyingForCorrectPlan = planRepository.findById(suscription.getPlanId())
				.map(p -> p.getType().equals(PlanType.PAID)).orElse(false);

		if (!suscription.isActive() || !isApplyingForCorrectPlan) {
			throw new CantCreatePromotion(getMessageTag("exceptions.promotions.cantCreate"));
		}

		boolean alreadyHasLinkedPromotion = promotionInstanceRepository
				.findByIdPromotionIdAndIdSuscriptionIdAndExpirationDateAfter(dto.getPromotionId(),
						dto.getSuscriptionId(), LocalDate.now())
				.isPresent();

		if (alreadyHasLinkedPromotion) {
			throw new CantCreatePromotion(getMessageTag("exceptions.promotions.cantCreate"));
		}

		Promotion linkedPromotion = repository.findById(dto.getPromotionId()).map(p -> p)
				.orElseThrow(() -> new CantCreatePromotion("exceptions.promotions.cantCreate"));

		if (!linkedPromotion.isEnabled()) {
			throw new CantCreatePromotion("exceptions.promotions.cantCreate");
		}

		LocalDate expirationDate = linkedPromotion.getExpirationMonths() != -1
				? LocalDate.now().plusMonths(linkedPromotion.getExpirationMonths())
				: null;

		PromotionInstance promotionInstance = new PromotionInstance(
				new PromotionInstanceId(dto.getSuscriptionId(), dto.getPromotionId()), expirationDate);
		promotionInstance.setPromotion(linkedPromotion);
		promotionInstance.setSubscription(suscriptionService.findSuscripcionById(dto.getSuscriptionId()));
		promotionInstance.setUserId(suscription.getUsuarioId());

		return promotionInstanceRepository.save(promotionInstance);
	}

	@Transactional
	public Optional<PromotionInstanceDTO> updatePromotionInstanceExpirationDate(Long promotionId, Long subscriptionId,
			LocalDate newExpirationDate) {
		return promotionInstanceRepository.findByIdPromotionIdAndIdSuscriptionId(promotionId, subscriptionId)
				.map(instance -> {
					instance.setExpirationDate(newExpirationDate);
					promotionInstanceRepository.save(instance);
					return new PromotionInstanceDTO(promotionId, subscriptionId, newExpirationDate);
				});
	}

}