package com.contractar.microserviciousuario.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.contractar.microservicioadapter.entities.SuscripcionAccesor;
import com.contractar.microservicioadapter.enums.PlanType;
import com.contractar.microserviciocommons.constants.controllers.DateControllerUrls;
import com.contractar.microserviciocommons.constants.controllers.PaymentControllerUrls;
import com.contractar.microserviciocommons.date.enums.DateFormatType;
import com.contractar.microserviciocommons.date.enums.DateOperationType;
import com.contractar.microserviciocommons.dto.SuscripcionDTO;
import com.contractar.microserviciocommons.dto.SuscriptionValidityDTO;
import com.contractar.microserviciocommons.dto.payment.PaymentInfoDTO;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciocommons.exceptions.proveedores.SuscriptionNotFound;
import com.contractar.microserviciocommons.exceptions.vendibles.CantCreateException;
import com.contractar.microserviciocommons.exceptions.vendibles.SubscriptionAlreadyExistsException;
import com.contractar.microserviciousuario.models.Plan;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.models.Suscripcion;
import com.contractar.microserviciousuario.repository.PlanRepository;
import com.contractar.microserviciousuario.repository.ProveedorRepository;
import com.contractar.microserviciousuario.repository.SuscripcionRepository;

@Service
public class ProveedorService {

	private PlanRepository planRepository;

	private ProveedorRepository proveedorRepository;

	private SuscripcionRepository suscripcionRepository;

	private UsuarioService usuarioService;

	private RestTemplate httpClient;

	@Value("${microservicio-commons.url}")
	private String microservicioCommonsUrl;

	@Value("${microservicio-payment.url}")
	private String microservicioPaymentUrl;

	@Value("${microservicio-config.url}")
	private String configServiceUrl;

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

	public ProveedorService(PlanRepository planRepository, ProveedorRepository proveedorRepository,
			SuscripcionRepository suscripcionRepository, RestTemplate httpClient, UsuarioService usuarioService) {
		this.planRepository = planRepository;
		this.proveedorRepository = proveedorRepository;
		this.suscripcionRepository = suscripcionRepository;
		this.httpClient = httpClient;
		this.usuarioService = usuarioService;
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

	public SuscripcionDTO createSuscripcion(Long proveedorId, Long planId)
			throws UserNotFoundException, CantCreateException {
		Proveedor proveedor = this.findById(proveedorId);

		Plan plan = planRepository.findById(planId).map(p -> p).orElseThrow(CantCreateException::new);

		if (suscripcionRepository.existsByUsuario_Id(proveedorId)) {
			throw new SubscriptionAlreadyExistsException(
					usuarioService.getMessageTag("exceptions.subscription.alreadyCreated"));
		}

		boolean isActive = plan.getType().equals(PlanType.FREE);

		Suscripcion suscripcion = new Suscripcion(isActive, proveedor, plan, LocalDate.now());

		suscripcionRepository.save(suscripcion);

		proveedor.setSuscripcion(suscripcion);
		proveedorRepository.save(proveedor);

		return new SuscripcionDTO(suscripcion.getId(), isActive, proveedorId, planId, suscripcion.getCreatedDate(),
				fetchDatePattern());

	}

	public SuscripcionDTO getSuscripcion(Long proveedorId) {
		try {
			SuscripcionAccesor suscription = Optional.ofNullable(this.findById(proveedorId).getSuscripcion())
					.map(s -> s)
					.orElseThrow(() -> new UserNotFoundException(getMessageTag("exceptions.user.notFound")));

			String datePattern = this.fetchDatePattern();

			SuscripcionDTO responseDTO = new SuscripcionDTO(suscription.getId(), suscription.isActive(), proveedorId,
					suscription.getPlan().getId(), suscription.getCreatedDate(), datePattern);

			Boolean isSuscriptionValid = httpClient
					.getForObject(microservicioPaymentUrl + PaymentControllerUrls.SUSCRIPTION_PAYMENT_BASE_URL
							.replace("{suscriptionId}", String.valueOf(suscription.getId())), Boolean.class);

			PaymentInfoDTO lastPaymentInfo = httpClient.getForObject(
					microservicioPaymentUrl + PaymentControllerUrls.LAST_SUSCRIPTION_PAYMENT_BASE_URL,
					PaymentInfoDTO.class);

			SuscriptionValidityDTO validity = new SuscriptionValidityDTO(isSuscriptionValid,
					lastPaymentInfo.getDate().plusMonths(1));

			responseDTO.setValidity(validity);

			return responseDTO;

		} catch (UserNotFoundException e) {
			return null;
		}

	}
}
