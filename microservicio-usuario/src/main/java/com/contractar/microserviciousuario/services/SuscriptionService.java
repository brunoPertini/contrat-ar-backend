package com.contractar.microserviciousuario.services;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.contractar.microservicioadapter.enums.PlanType;
import com.contractar.microservicioadapter.enums.PromotionType;
import com.contractar.microserviciocommons.constants.controllers.DateControllerUrls;
import com.contractar.microserviciocommons.constants.controllers.PaymentControllerUrls;
import com.contractar.microserviciocommons.date.enums.DateFormatType;
import com.contractar.microserviciocommons.date.enums.DateOperationType;
import com.contractar.microserviciocommons.dto.SuscripcionDTO;
import com.contractar.microserviciocommons.dto.SuscriptionValidityDTO;
import com.contractar.microserviciocommons.dto.UserPromotionDTO;
import com.contractar.microserviciocommons.dto.payment.PaymentInfoDTO;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciocommons.exceptions.proveedores.SuscriptionNotFound;
import com.contractar.microserviciousuario.models.Plan;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.models.Suscripcion;
import com.contractar.microserviciousuario.repository.PlanRepository;
import com.contractar.microserviciousuario.repository.ProveedorRepository;
import com.contractar.microserviciousuario.repository.SuscripcionRepository;

@Service
public class SuscriptionService {
	private RestTemplate httpClient;

	private PlanRepository planRepository;

	private ProveedorRepository proveedorRepository;

	private SuscripcionRepository suscripcionRepository;

	@Value("${microservicio-commons.url}")
	private String microservicioCommonsUrl;

	@Value("${microservicio-payment.url}")
	private String microservicioPaymentUrl;

	@Value("${microservicio-usuario.url}")
	private String microservicioUsuarioUrl;

	private final Map<PromotionType, Function<UserPromotionDTO, Boolean>> byPromotionSuscriptionValidityResolver;

	private final Map<PromotionType, Function<UserPromotionDTO, LocalDate>> byPromotionSuscriptionExpirationDateResolver;

	private final Map<PromotionType, Function<UserPromotionDTO, Boolean>> byPromotionCanBePayedResolver;

	public SuscriptionService(RestTemplate httpClient, PlanRepository planRepository,
			ProveedorRepository proveedorRepository, SuscripcionRepository suscripcionRepository) {
		this.httpClient = httpClient;
		this.planRepository = planRepository;
		this.proveedorRepository = proveedorRepository;
		this.suscripcionRepository = suscripcionRepository;

		this.byPromotionSuscriptionValidityResolver = Map.of(PromotionType.FULL_DISCOUNT_FOREVER, p -> true,
				PromotionType.FULL_DISCOUNT_MONTHS,
				promotionInfo -> promotionInfo.getExpirationDate().isAfter(LocalDate.now()));

		this.byPromotionSuscriptionExpirationDateResolver = Map.of(PromotionType.FULL_DISCOUNT_FOREVER, p -> null,
				PromotionType.FULL_DISCOUNT_MONTHS, promotionInfo -> promotionInfo.getExpirationDate());

		this.byPromotionCanBePayedResolver = Map.of(PromotionType.FULL_DISCOUNT_FOREVER, p -> false,
				PromotionType.FULL_DISCOUNT_MONTHS, promotionInfo -> {
					LocalDate minimalDate = promotionInfo.getExpirationDate().minusDays(10);
					return LocalDate.now().isAfter(minimalDate);
				});

	}

	private PaymentInfoDTO fetchLastSuccessfulPaymentInfo(Long suscriptionId) {

		return httpClient.getForObject(microservicioPaymentUrl + PaymentControllerUrls.LAST_SUSCRIPTION_PAYMENT_BASE_URL
				.replace("{suscriptionId}", String.valueOf(suscriptionId)), PaymentInfoDTO.class);
	}

	private String fetchDatePattern() {
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(microservicioCommonsUrl)
				.path(DateControllerUrls.DATES_BASE_URL).queryParam("operation", DateOperationType.FORMAT)
				.queryParam("format", DateFormatType.FULL);

		return httpClient.getForObject(uriBuilder.toUriString(), String.class);
	}

	private Optional<Proveedor> getProveedor(Long id) {
		return proveedorRepository.findById(id);
	}

	private boolean resolveSuscriptionValidity(Long suscriptionId, UserPromotionDTO promotionInfo) {
		Supplier<Boolean> isSuscriptionValidSupplier = () -> httpClient.getForObject(microservicioPaymentUrl
				+ PaymentControllerUrls.IS_SUSCRIPTION_VALID.replace("{suscriptionId}", String.valueOf(suscriptionId)),
				Boolean.class);

		return Optional.ofNullable(promotionInfo).map(p -> {
			boolean byPromotionResult = byPromotionSuscriptionValidityResolver.get(p.getPromotionType())
					.apply(promotionInfo);
			return byPromotionResult || isSuscriptionValidSupplier.get();
		}).orElseGet(isSuscriptionValidSupplier);

	}

	private LocalDate resolveSuscriptionExpirationDate(Long suscriptionId, UserPromotionDTO promotionInfo) {

		if (promotionInfo != null) {
			return byPromotionSuscriptionExpirationDateResolver.get(promotionInfo.getPromotionType()).apply(promotionInfo);
		}

		PaymentInfoDTO lastPaymentInfo = this.fetchLastSuccessfulPaymentInfo(suscriptionId);
		return Optional.ofNullable(lastPaymentInfo).map(optPaymentInfo -> Optional.ofNullable(optPaymentInfo.getDate())
				.map(expDate -> expDate.plusMonths(1)).orElse(null)).orElse(null);
	}

	private boolean resolveCanSubscriptionBePayed(Long suscriptionId, UserPromotionDTO promotionInfo) {
		return Optional.ofNullable(promotionInfo)
				.map(p -> byPromotionCanBePayedResolver.get(p.getPromotionType())
						.apply(promotionInfo))
				.orElseGet(() -> httpClient
						.getForObject(microservicioPaymentUrl + PaymentControllerUrls.IS_SUSCRIPTION_PAYABLE
								.replace("{suscriptionId}", suscriptionId.toString()), Boolean.class));
	}

	public Suscripcion findSuscripcionById(Long id) throws SuscriptionNotFound {
		return this.suscripcionRepository.findById(id).map(s -> s).orElseThrow(() -> new SuscriptionNotFound(""));
	}

	private SuscripcionDTO getSuscripcionDTO(Proveedor proveedor, Suscripcion suscription, boolean shouldCheckValidity,
			UserPromotionDTO promotionInfo) {
		String datePattern = this.fetchDatePattern();

		SuscripcionDTO responseDTO = new SuscripcionDTO(suscription.getId(), suscription.isActive(), proveedor.getId(),
				suscription.getPlan().getId(), suscription.getCreatedDate(), datePattern);

		if (shouldCheckValidity) {

			boolean isSuscriptionValid = resolveSuscriptionValidity(suscription.getId(), promotionInfo);

			LocalDate validityExpirationDate = resolveSuscriptionExpirationDate(suscription.getId(), promotionInfo);

			SuscriptionValidityDTO validity = new SuscriptionValidityDTO(isSuscriptionValid, validityExpirationDate);

			boolean canBePayed = resolveCanSubscriptionBePayed(suscription.getId(), promotionInfo);
			validity.setCanBePayed(canBePayed);

			boolean hasFreePlan = proveedor.getSuscripcion().getPlan().getType().equals(PlanType.FREE);

			if (!validity.isValid() && !hasFreePlan) {
				Plan freePlan = planRepository.findById(1L).map(p -> p).orElse(null);
				proveedor.getSuscripcion().setPlan(freePlan);
				proveedorRepository.save(proveedor);

				responseDTO.setPlanId(freePlan.getId());
				responseDTO.setPlanPrice(freePlan.getPrice());
			}

			responseDTO.setValidity(validity);

		}

		return responseDTO;

	}

	public SuscripcionDTO getSuscripcionById(Long suscriptionId, boolean shouldCheckValidity) {
		try {
			Suscripcion suscription = this.findSuscripcionById(suscriptionId);

			return this.getSuscripcionDTO((Proveedor) suscription.getUsuario(), suscription, shouldCheckValidity, null);

		} catch (SuscriptionNotFound e) {
			return null;
		}
	}

	public SuscripcionDTO getSuscripcion(Long proveedorId) {
		try {
			Proveedor proveedor = getProveedor(proveedorId).map(p -> p).orElseThrow(UserNotFoundException::new);
			Suscripcion suscription = this.findSuscripcionById(proveedor.getSuscripcion().getId());
			return this.getSuscripcionDTO(proveedor, suscription, true, null);
		} catch (SuscriptionNotFound | UserNotFoundException e) {
			return null;
		}
	}

	public SuscripcionDTO getSuscripcion(Long proveedorId, UserPromotionDTO promotionInfo) {
		if (promotionInfo == null) {
			return getSuscripcion(proveedorId);
		}

		try {
			Proveedor proveedor = getProveedor(proveedorId).map(p -> p).orElseThrow(UserNotFoundException::new);
			Suscripcion suscription = this.findSuscripcionById(proveedor.getSuscripcion().getId());
			return this.getSuscripcionDTO(proveedor, suscription, true, promotionInfo);
		} catch (SuscriptionNotFound | UserNotFoundException e) {
			return null;
		}

	}
}
