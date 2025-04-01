package com.contractar.microserviciousuario.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.contractar.microservicioadapter.entities.SuscripcionAccesor;
import com.contractar.microservicioadapter.enums.PlanType;
import com.contractar.microserviciocommons.constants.controllers.DateControllerUrls;
import com.contractar.microserviciocommons.constants.controllers.PaymentControllerUrls;
import com.contractar.microserviciocommons.constants.controllers.UsersControllerUrls;
import com.contractar.microserviciocommons.date.enums.DateFormatType;
import com.contractar.microserviciocommons.date.enums.DateOperationType;
import com.contractar.microserviciocommons.dto.SuscripcionDTO;
import com.contractar.microserviciocommons.dto.SuscriptionActiveUpdateDTO;
import com.contractar.microserviciocommons.dto.SuscriptionValidityDTO;
import com.contractar.microserviciocommons.dto.payment.PaymentInfoDTO;
import com.contractar.microserviciocommons.exceptions.CantCreateSuscription;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciocommons.exceptions.proveedores.SuscriptionNotFound;
import com.contractar.microserviciocommons.mailing.PlanChangeConfirmation;
import com.contractar.microserviciousuario.admin.services.AdminService;
import com.contractar.microserviciousuario.models.Plan;
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

	private RestTemplate httpClient;

	@Value("${microservicio-commons.url}")
	private String microservicioCommonsUrl;

	@Value("${microservicio-payment.url}")
	private String microservicioPaymentUrl;

	@Value("${microservicio-config.url}")
	private String configServiceUrl;

	@Value("${microservicio-mailing.url}")
	private String mailingServiceUrl;

	public ProveedorService(PlanRepository planRepository, ProveedorRepository proveedorRepository,
			SuscripcionRepository suscripcionRepository, RestTemplate httpClient, AdminService adminService) {
		this.planRepository = planRepository;
		this.proveedorRepository = proveedorRepository;
		this.suscripcionRepository = suscripcionRepository;
		this.httpClient = httpClient;
		this.adminService = adminService;
	}

	private String fetchDatePattern() {
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(microservicioCommonsUrl)
				.path(DateControllerUrls.DATES_BASE_URL).queryParam("operation", DateOperationType.FORMAT)
				.queryParam("format", DateFormatType.FULL);

		return httpClient.getForObject(uriBuilder.toUriString(), String.class);
	}

	private String getMessageTag(String tagId) {
		final String fullUrl = configServiceUrl + "/i18n/" + tagId;
		return httpClient.getForObject(fullUrl, String.class);
	}

	public Proveedor findById(Long proveedorId) throws UserNotFoundException {

		return proveedorRepository.findById(proveedorId).map(proveedor -> proveedor)
				.orElseThrow(UserNotFoundException::new);
	}

	public List<Plan> findAll() {
		return planRepository.findAll();
	}

	public Suscripcion findSuscripcionById(Long id) throws SuscriptionNotFound {
		return this.suscripcionRepository.findById(id).map(s -> s).orElseThrow(() -> new SuscriptionNotFound(""));
	}

	private PaymentInfoDTO fetchLastSuccessfulPaymentInfo(Long suscriptionId) {

		return httpClient.getForObject(microservicioPaymentUrl + PaymentControllerUrls.LAST_SUSCRIPTION_PAYMENT_BASE_URL
				.replace("{suscriptionId}", String.valueOf(suscriptionId)), PaymentInfoDTO.class);
	}

	private Suscripcion createPaidPlanSuscription(Proveedor proveedor) {
		Plan paidPlan = planRepository.findByType(PlanType.PAID).get();
		Suscripcion suscripcion = new Suscripcion(true, proveedor, paidPlan, LocalDate.now());

		return suscripcionRepository.save(suscripcion);

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
		} else {
			proveedor.setSuscripcion(createdSuscripcion);
			proveedorRepository.save(proveedor);
		}

		notifyPlanChange(proveedor.getEmail(), proveedor.getName(), PlanType.FREE.name());

		return createdSuscripcion;

	}

	public SuscripcionDTO createSuscripcion(Long proveedorId, Long planId)
			throws UserNotFoundException, CantCreateSuscription {
		Proveedor proveedor = this.findById(proveedorId);

		Plan plan = planRepository.findById(planId).map(p -> p)
				.orElseThrow(() -> new CantCreateSuscription(getMessageTag("exception.suscription.cantCreate")));

		boolean isSignupContext = proveedor.getSuscripcion() == null;

		boolean isTheSamePlan = !isSignupContext && plan.getId().equals(proveedor.getSuscripcion().getPlan().getId());

		if (isTheSamePlan) {
			throw new CantCreateSuscription(getMessageTag("exception.suscription.cantCreate"));
		}

		Suscripcion temporalCreatedSuscription = plan.getType().equals(PlanType.PAID)
				? createPaidPlanSuscription(proveedor)
				: createFreePlanSuscription(proveedor);

		return new SuscripcionDTO(temporalCreatedSuscription.getId(), temporalCreatedSuscription.isActive(),
				proveedorId, planId, temporalCreatedSuscription.getCreatedDate(), fetchDatePattern());

	}

	public SuscripcionDTO getSuscripcion(Long proveedorId) {
		try {
			Proveedor proveedor = this.findById(proveedorId);
			SuscripcionAccesor suscription = proveedor.getSuscripcion();

			String datePattern = this.fetchDatePattern();

			SuscripcionDTO responseDTO = new SuscripcionDTO(suscription.getId(), suscription.isActive(), proveedorId,
					suscription.getPlan().getId(), suscription.getCreatedDate(), datePattern);

			Boolean isSuscriptionValid = httpClient
					.getForObject(microservicioPaymentUrl + PaymentControllerUrls.IS_SUSCRIPTION_VALID
							.replace("{suscriptionId}", String.valueOf(suscription.getId())), Boolean.class);

			PaymentInfoDTO lastPaymentInfo = this.fetchLastSuccessfulPaymentInfo(suscription.getId());

			LocalDate validityExpirationDate = Optional.ofNullable(lastPaymentInfo).map(optPaymentInfo -> Optional
					.ofNullable(optPaymentInfo.getDate()).map(expDate -> expDate.plusMonths(1)).orElse(null))
					.orElse(null);

			SuscriptionValidityDTO validity = new SuscriptionValidityDTO(isSuscriptionValid, validityExpirationDate);

			boolean canBePayed = httpClient
					.getForObject(microservicioPaymentUrl + PaymentControllerUrls.IS_SUSCRIPTION_PAYABLE
							.replace("{suscriptionId}", suscription.getId().toString()), Boolean.class);
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

			return responseDTO;

		} catch (UserNotFoundException e) {
			return null;
		}

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
					notifyPlanChange(proveedor.getEmail(), proveedor.getName(),
							PlanType.PAID.name());
				}

			});
		});
	}
}
