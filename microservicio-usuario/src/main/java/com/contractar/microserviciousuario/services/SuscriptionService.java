package com.contractar.microserviciousuario.services;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.contractar.microservicioadapter.enums.PlanType;
import com.contractar.microserviciocommons.constants.controllers.DateControllerUrls;
import com.contractar.microserviciocommons.constants.controllers.PaymentControllerUrls;
import com.contractar.microserviciocommons.constants.controllers.ProveedorControllerUrls;
import com.contractar.microserviciocommons.date.enums.DateFormatType;
import com.contractar.microserviciocommons.date.enums.DateOperationType;
import com.contractar.microserviciocommons.dto.SuscripcionDTO;
import com.contractar.microserviciocommons.dto.SuscriptionValidityDTO;
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

	public SuscriptionService(RestTemplate httpClient, PlanRepository planRepository,
			ProveedorRepository proveedorRepository, SuscripcionRepository suscripcionRepository) {
		this.httpClient = httpClient;
		this.planRepository = planRepository;
		this.proveedorRepository = proveedorRepository;
		this.suscripcionRepository = suscripcionRepository;
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
		try {
			String url = microservicioUsuarioUrl
					+ ProveedorControllerUrls.PROVEEDOR_BASE_URL.replace("{proveedorId}", id.toString());

			return Optional.of(httpClient.getForObject(url, Proveedor.class));
		} catch (Exception e) {
			return Optional.empty();
		}

	}

	public Suscripcion findSuscripcionById(Long id) throws SuscriptionNotFound {
		return this.suscripcionRepository.findById(id).map(s -> s).orElseThrow(() -> new SuscriptionNotFound(""));
	}

	private SuscripcionDTO getSuscripcionDTO(Proveedor proveedor, Suscripcion suscription,
			boolean shouldCheckValidity) {
		String datePattern = this.fetchDatePattern();

		SuscripcionDTO responseDTO = new SuscripcionDTO(suscription.getId(), suscription.isActive(), proveedor.getId(),
				suscription.getPlan().getId(), suscription.getCreatedDate(), datePattern);

		if (shouldCheckValidity) {
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

		}

		return responseDTO;

	}

	public SuscripcionDTO getSuscripcionById(Long suscriptionId, boolean shouldCheckValidity) {
		try {
			Suscripcion suscription = this.findSuscripcionById(suscriptionId);

			return this.getSuscripcionDTO((Proveedor) suscription.getUsuario(), suscription, shouldCheckValidity);

		} catch (SuscriptionNotFound e) {
			return null;
		}
	}

	public SuscripcionDTO getSuscripcion(Long proveedorId) {
		try {
			Proveedor proveedor = getProveedor(proveedorId).map(p -> p).orElseThrow(UserNotFoundException::new);
			Suscripcion suscription = this.findSuscripcionById(proveedor.getSuscripcion().getId());
			return this.getSuscripcionDTO(proveedor, suscription, true);
		} catch (SuscriptionNotFound | UserNotFoundException e) {
			return null;
		}
	}
}
