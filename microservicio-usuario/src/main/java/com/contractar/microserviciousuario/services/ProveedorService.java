package com.contractar.microserviciousuario.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.contractar.microservicioadapter.enums.PlanType;
import com.contractar.microservicioadapter.enums.PromotionType;
import com.contractar.microserviciocommons.constants.controllers.DateControllerUrls;
import com.contractar.microserviciocommons.constants.controllers.PaymentControllerUrls;
import com.contractar.microserviciocommons.constants.controllers.PromotionControllerUrls;
import com.contractar.microserviciocommons.constants.controllers.UsersControllerUrls;
import com.contractar.microserviciocommons.date.enums.DateFormatType;
import com.contractar.microserviciocommons.date.enums.DateOperationType;
import com.contractar.microserviciocommons.dto.PlanDTO;
import com.contractar.microserviciocommons.dto.SuscripcionDTO;
import com.contractar.microserviciocommons.dto.SuscriptionActiveUpdateDTO;
import com.contractar.microserviciocommons.dto.payment.PaymentInfoDTO;
import com.contractar.microserviciocommons.dto.usuario.PromotionInstanceCreate;
import com.contractar.microserviciocommons.exceptions.CantCreateSuscription;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciocommons.mailing.PlanChangeConfirmation;
import com.contractar.microserviciousuario.admin.services.AdminService;
import com.contractar.microserviciousuario.models.Plan;
import com.contractar.microserviciousuario.models.Promotion;
import com.contractar.microserviciousuario.models.PromotionInstance;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.models.Suscripcion;
import com.contractar.microserviciousuario.repository.PlanRepository;
import com.contractar.microserviciousuario.repository.ProveedorRepository;
import com.contractar.microserviciousuario.repository.SuscripcionRepository;

import jakarta.transaction.Transactional;

@Service
public class ProveedorService {

	private PlanRepository planRepository;

	private ProveedorRepository proveedorRepository;

	private SuscripcionRepository suscripcionRepository;

	private AdminService adminService;

	private PromotionService promotionService;

	private RestTemplate httpClient;

	@Value("${microservicio-commons.url}")
	private String microservicioCommonsUrl;

	@Value("${microservicio-payment.url}")
	private String microservicioPaymentUrl;

	@Value("${microservicio-config.url}")
	private String configServiceUrl;

	@Value("${microservicio-mailing.url}")
	private String mailingServiceUrl;

	@Value("${microservicio-usuario.url}")
	private String microservicioUsuarioUrl;

	public ProveedorService(PlanRepository planRepository, ProveedorRepository proveedorRepository,
			SuscripcionRepository suscripcionRepository, RestTemplate httpClient, AdminService adminService,
			PromotionService promotionService) {
		this.planRepository = planRepository;
		this.proveedorRepository = proveedorRepository;
		this.suscripcionRepository = suscripcionRepository;
		this.httpClient = httpClient;
		this.adminService = adminService;
		this.promotionService = promotionService;
	}

	private String fetchDatePattern() {
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(microservicioCommonsUrl)
				.path(DateControllerUrls.DATES_BASE_URL).queryParam("operation", DateOperationType.FORMAT)
				.queryParam("format", DateFormatType.FULL);

		return httpClient.getForObject(uriBuilder.toUriString(), String.class);
	}

	private HttpHeaders getCurrentRequestHeaders() {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();

		HttpHeaders headers = new HttpHeaders();

		headers.setBasicAuth(requestAttributes.getRequest().getHeader("Authorization"));

		return headers;
	}

	public String getMessageTag(String tagId) {
		final String fullUrl = configServiceUrl + "/i18n/" + tagId;
		return httpClient.getForObject(fullUrl, String.class);
	}

	public Proveedor findById(Long proveedorId) throws UserNotFoundException {

		return proveedorRepository.findById(proveedorId).map(proveedor -> proveedor)
				.orElseThrow(UserNotFoundException::new);
	}

	public List<PlanDTO> findAllPlans() {
		boolean areApplicablePromotions = !promotionService.findAll().isEmpty();
		return planRepository.findAll().stream().map(p -> {
			PlanDTO planDTO = new PlanDTO(p.getId(), p.getDescripcion(), p.getType(), p.getPrice());
			if (p.getType().equals(PlanType.PAID) && areApplicablePromotions) {
				Promotion applicablePromotion = promotionService.findCurrentApplicable();
				BigDecimal discountPercentage = applicablePromotion.getDiscountPercentage();
				BigDecimal discountPriceDecimal = BigDecimal.valueOf(p.getPrice())
						.subtract((BigDecimal.valueOf(p.getPrice()).multiply(discountPercentage)));

				double discountPrice = discountPriceDecimal.setScale(0, RoundingMode.DOWN).intValue();
				planDTO.setPriceWithDiscount(discountPrice);
				planDTO.setApplicablePromotion(applicablePromotion.getId());

			}

			return planDTO;

		}).collect(Collectors.toList());
	}

	private PaymentInfoDTO fetchLastSuccessfulPaymentInfo(Long suscriptionId) {

		return httpClient.getForObject(microservicioPaymentUrl + PaymentControllerUrls.LAST_SUSCRIPTION_PAYMENT_BASE_URL
				.replace("{suscriptionId}", String.valueOf(suscriptionId)), PaymentInfoDTO.class);
	}

	private void notifyPlanChange(String email, String name, String destinyPlan) {
		String destinyPlanTranslated = getMessageTag("plan." + destinyPlan);

		PlanChangeConfirmation mailBody = new PlanChangeConfirmation(email, name, destinyPlanTranslated);

		try {
			httpClient.postForEntity(mailingServiceUrl + UsersControllerUrls.PLAN_CHANGE_SUCCESS_EMAIL, mailBody,
					Void.class);
		} catch (ResourceAccessException e) {
			System.out.println(e.getMessage());
		}
	}

	@Transactional
	private Suscripcion createPaidPlanSuscription(Proveedor proveedor, Optional<Long> promotionIdOpt)
			throws CantCreateSuscription {
		Plan paidPlan = planRepository.findByType(PlanType.PAID).get();
		Optional<PromotionType> promotionTypeOpt = promotionIdOpt
				.flatMap(promotionId -> promotionService.findById(promotionId).map(p -> p.getType()));

		boolean isApplyingFullDiscountPromotion = promotionTypeOpt
				.map(type -> type.equals(PromotionType.FULL_DISCOUNT_FOREVER) || type.equals(PromotionType.FULL_DISCOUNT_MONTHS))
				.orElse(false);

		boolean isSuscriptionActive = promotionTypeOpt.isEmpty() || isApplyingFullDiscountPromotion;

		Suscripcion suscripcion = new Suscripcion(isSuscriptionActive, proveedor, paidPlan, LocalDate.now());

		boolean isSignupContext = proveedor.getSuscripcion() == null;

		Suscripcion createdSuscripcion = suscripcionRepository.save(suscripcion);

		if (isSignupContext || isApplyingFullDiscountPromotion) {
			proveedor.setSuscripcion(createdSuscripcion);
			proveedorRepository.save(proveedor);
		}

		return createdSuscripcion;

	}

	@Transactional
	private Suscripcion createFreePlanSuscription(Proveedor proveedor) throws CantCreateSuscription {
		Plan freePlan = planRepository.findByType(PlanType.FREE).get();

		boolean isSignupContext = proveedor.getSuscripcion() == null;

		LocalDate createdDate;

		if (!isSignupContext) {
			Long subscriptioId = proveedor.getSuscripcion().getId();

			PaymentInfoDTO pInfo = this.fetchLastSuccessfulPaymentInfo(subscriptioId);

			createdDate = Optional.ofNullable(pInfo.getDate()).map(paymentDate -> paymentDate.plusMonths(1))
					.orElse(LocalDate.now());
		} else {
			createdDate = LocalDate.now();
		}

		Suscripcion suscripcion = new Suscripcion(true, proveedor, freePlan, createdDate);

		Suscripcion createdSuscripcion = suscripcionRepository.save(suscripcion);

		if (!isSignupContext) {
			adminService.addChangeRequestEntry(proveedor.getId(), createdSuscripcion.getId());
			notifyPlanChange(proveedor.getEmail(), proveedor.getName(), PlanType.FREE.name());
		} else {
			proveedor.setSuscripcion(createdSuscripcion);
			proveedorRepository.save(proveedor);
		}

		return createdSuscripcion;

	}

	public SuscripcionDTO createSuscripcion(Long proveedorId, Long planId, Optional<Long> promotionIdOpt)
			throws UserNotFoundException, CantCreateSuscription {
		Proveedor proveedor = this.findById(proveedorId);

		Plan plan = planRepository.findById(planId).map(p -> p)
				.orElseThrow(() -> new CantCreateSuscription(getMessageTag("exception.suscription.cantCreate")));

		boolean isSignupContext = proveedor.getSuscripcion() == null;

		boolean isTheSamePlan = !isSignupContext && plan.getId().equals(proveedor.getSuscripcion().getPlan().getId());

		boolean isPaidPlan = plan.getType().equals(PlanType.PAID);

		if (isTheSamePlan) {
			throw new CantCreateSuscription(getMessageTag("exception.suscription.cantCreate"));
		}

		Suscripcion temporalCreatedSuscription = isPaidPlan ? createPaidPlanSuscription(proveedor, promotionIdOpt)
				: createFreePlanSuscription(proveedor);

		if (isPaidPlan && promotionIdOpt.isPresent()) {

			try {
				HttpHeaders headers = getCurrentRequestHeaders();

				String url = microservicioUsuarioUrl + PromotionControllerUrls.PROMOTION_BASE_URL
						+ PromotionControllerUrls.PROMOTION_INSTANCE_BASE_URL;

				HttpEntity<PromotionInstanceCreate> entity = new HttpEntity<>(
						new PromotionInstanceCreate(temporalCreatedSuscription.getId(), promotionIdOpt.get(), proveedorId), headers);

				PromotionInstance createdPromotionInstance = httpClient
						.exchange(url, HttpMethod.POST, entity, PromotionInstance.class).getBody();

				PromotionType createdPromotionType = createdPromotionInstance.getPromotion().getType();

				boolean isAFreeTypePromotion = createdPromotionType.equals(PromotionType.FULL_DISCOUNT_FOREVER)
						|| createdPromotionType.equals(PromotionType.FULL_DISCOUNT_MONTHS);

				if (isAFreeTypePromotion) {
					this.updateLinkedSubscription(proveedorId,
							new SuscriptionActiveUpdateDTO(temporalCreatedSuscription.getId(), true));

				}

			} catch (HttpClientErrorException | HttpServerErrorException e) {
				System.out.println("Could not create promotion for suscription: " + temporalCreatedSuscription.getId());
			}
		}

		return new SuscripcionDTO(temporalCreatedSuscription.getId(), temporalCreatedSuscription.isActive(),
				proveedorId, planId, temporalCreatedSuscription.getCreatedDate(), fetchDatePattern());

	}

	@Transactional
	public void updateLinkedSubscription(Long userId, SuscriptionActiveUpdateDTO dto) {
		proveedorRepository.findById(userId).ifPresent(proveedor -> {
			suscripcionRepository.findById(dto.getId()).ifPresent(subscription -> {
				subscription.setActive(dto.isActive());
				subscription.setUsuario(proveedor);

				suscripcionRepository.save(subscription);

				if (dto.isActive()) {
					proveedor.setSuscripcion(subscription);
					proveedorRepository.save(proveedor);
					notifyPlanChange(proveedor.getEmail(), proveedor.getName(), PlanType.PAID.name());
				}

			});
		});
	}
}