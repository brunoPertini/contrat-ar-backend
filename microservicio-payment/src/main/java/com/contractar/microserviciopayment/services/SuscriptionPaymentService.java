package com.contractar.microserviciopayment.services;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.contractar.microserviciocommons.constants.controllers.ProveedorControllerUrls;
import com.contractar.microserviciocommons.dto.SuscripcionDTO;
import com.contractar.microserviciocommons.dto.payment.PaymentInfoDTO;
import com.contractar.microserviciocommons.dto.payment.PaymentsResponseDTO;
import com.contractar.microserviciocommons.exceptions.proveedores.SuscriptionNotFound;
import com.contractar.microserviciopayment.dtos.PaymentCreateDTO;
import com.contractar.microserviciopayment.models.PaymentProvider;
import com.contractar.microserviciopayment.models.PaymentState;
import com.contractar.microserviciopayment.models.SuscriptionPayment;
import com.contractar.microserviciopayment.models.enums.IntegrationType;
import com.contractar.microserviciopayment.models.enums.UalaPaymentStateValue;
import com.contractar.microserviciopayment.repository.PaymentStateRepository;
import com.contractar.microserviciopayment.repository.SuscriptionPaymentRepository;
import com.contractar.microserviciopayment.repository.UalaPaymentStateRepository;
import com.contractar.microserviciousuario.models.Suscripcion;

@Service
public class SuscriptionPaymentService {
	private SuscriptionPaymentRepository repository;

	@Value("${microservicio-config.url}")
	private String configServiceUrl;

	@Value("${microservicio-usuario.url}")
	private String microservicioUsuarioUrl;

	private RestTemplate httpClient;

	private ProviderServiceImplFactory providerServiceImplFactory;

	private UalaPaymentStateRepository ualaPaymentStateRepository;
	
	private PaymentStateRepository paymentStateRepository;

	public SuscriptionPaymentService(SuscriptionPaymentRepository repository,
			ProviderServiceImplFactory providerServiceImplFactory,
			RestTemplate httpClient,
			UalaPaymentStateRepository ualaPaymentStateRepository,
			PaymentStateRepository paymentStateRepository) {
		this.repository = repository;
		this.providerServiceImplFactory = providerServiceImplFactory;
		this.ualaPaymentStateRepository = ualaPaymentStateRepository;
		this.httpClient = httpClient;
		this.paymentStateRepository = paymentStateRepository;
	}

	private String getMessageTag(String tagId) {
		final String fullUrl = configServiceUrl + "/i18n/" + tagId;
		return httpClient.getForObject(fullUrl, String.class);
	}

	private Suscripcion getSuscription(Long suscripcionId) throws SuscriptionNotFound {
		try {
			String url = microservicioUsuarioUrl
					+ ProveedorControllerUrls.GET_SUSCRIPCION.replace("{suscriptionId}", suscripcionId.toString());

			UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url).queryParam("getAsEntity", "true");

			return httpClient.getForObject(uriBuilder.toUriString(), Suscripcion.class);
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode().equals(HttpStatusCode.valueOf(404))) {
				throw new SuscriptionNotFound(getMessageTag("exception.suscription.notFound"));
			}

			throw e;
		}
	}

	private SuscripcionDTO getSuscriptionDTO(Long suscripcionId) throws SuscriptionNotFound {
		try {
			String url = microservicioUsuarioUrl
					+ ProveedorControllerUrls.GET_SUSCRIPCION.replace("{suscriptionId}", suscripcionId.toString());

			UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url).queryParam("getAsEntity", "false");

			return httpClient.getForObject(uriBuilder.toUriString(), SuscripcionDTO.class);
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode().equals(HttpStatusCode.valueOf(404))) {
				throw new SuscriptionNotFound(getMessageTag("exception.suscription.notFound"));
			}

			throw e;
		}
	}

	public SuscriptionPayment createPayment(PaymentCreateDTO dto, PaymentProvider currentProvider, Long suscripcionId, Long promotionId)
			throws SuscriptionNotFound {
		if (currentProvider == null) {
			throw new RuntimeException("Payment provider not set");
		}

		if (!currentProvider.getIntegrationType().equals(IntegrationType.OUTSITE)) {
			throw new RuntimeException("Invalid integration type");
		}

		Suscripcion suscription = this.getSuscription(suscripcionId);

		com.contractar.microserviciopayment.providers.OutsitePaymentProvider paymentProviderImpl = providerServiceImplFactory
				.getOutsitePaymentProvider();

		LocalDate paymentDate;

		SuscriptionPayment newPayment = new SuscriptionPayment(dto.getExternalId(), dto.getPaymentPeriod(),
				LocalDate.now(), dto.getAmount(), dto.getCurrency(), currentProvider, null);

		newPayment.setSuscripcion(suscription);

		paymentProviderImpl.setPaymentAsPending(newPayment);
		Optional.ofNullable(promotionId).ifPresent(newPayment::setPromotionId);
		return repository.save(newPayment);
	}

	public boolean isSuscriptionValid(Long suscriptionId) {
		SuscripcionDTO subscription;

		try {
			subscription = this.getSuscriptionDTO(suscriptionId);
		} catch (SuscriptionNotFound e) {
			return false;
		}

		boolean isFreePlan = subscription.getPlanPrice() == 0;

		if (isFreePlan) {
			return true;
		}

		// TODO: handle non OUTSITE providers
		com.contractar.microserviciopayment.providers.OutsitePaymentProvider currentProviderImpl = providerServiceImplFactory
				.getOutsitePaymentProvider();

		PaymentState successPaymentState = ualaPaymentStateRepository.findByState(UalaPaymentStateValue.APPROVED.name()).get();

		Optional<SuscriptionPayment> lastPaymentOpt = repository
				.findTopBySuscripcionIdAndStateOrderByPaymentPeriodDesc(suscriptionId, successPaymentState);

		if (lastPaymentOpt.isEmpty()) {
			return false;
		}

		SuscriptionPayment payment = lastPaymentOpt.get();

		YearMonth paymentPeriod = payment.getPaymentPeriod();

		Month paymentMonth = paymentPeriod.getMonth();

		Month currentMonth = YearMonth.now().getMonth();

		boolean wasPaymentDoneAtExpectedMonth = paymentMonth.equals(currentMonth) 
				|| paymentMonth.equals(currentMonth.plus(1))
				|| (paymentMonth.equals(currentMonth.minus(1))
						&& payment.getDate().plusMonths(1).isAfter(LocalDate.now()));

		return wasPaymentDoneAtExpectedMonth && (currentProviderImpl.wasPaymentAccepted(payment) || currentProviderImpl.isPaymentProcessed(payment));
	}

	public boolean canSuscriptionBePayed(Long suscriptionId) throws SuscriptionNotFound {
		boolean hasFreePlan = this.getSuscriptionDTO(suscriptionId).getPlanPrice() == 0;

		if (hasFreePlan) {
			return false;
		}

		// TODO: handle non OUTSITE providers
		com.contractar.microserviciopayment.providers.OutsitePaymentProvider currentProviderImpl = providerServiceImplFactory
				.getOutsitePaymentProvider();

		Optional<SuscriptionPayment> lastPaymentOpt = repository
				.findTopBySuscripcionIdOrderByPaymentPeriodDesc(suscriptionId);

		// First pay to be made
		if (lastPaymentOpt.isEmpty()) {
			return true;
		}

		SuscriptionPayment lastPayment = lastPaymentOpt.get();

		YearMonth paymentPeriod = lastPayment.getPaymentPeriod() != null ? lastPayment.getPaymentPeriod().plusMonths(1)
				: YearMonth.now();

		// User is paying some previous expired period
		boolean isPayingPreviousPeriod = paymentPeriod.isBefore(YearMonth.now());

		if (isPayingPreviousPeriod) {
			return true;
		}

		if (currentProviderImpl.wasPaymentRejected(lastPayment) || currentProviderImpl.isPaymentPending(lastPayment)) {
			return true;
		}

		if (currentProviderImpl.isPaymentProcessed(lastPayment)) {
			return false;
		}

		LocalDate suscriptionExpirationDate = lastPayment.getDate().plusMonths(1);

		LocalDate minimalPayDate = suscriptionExpirationDate.minusDays(10);

		LocalDate today = LocalDate.now();

		// If it may be missing days for minimalPayDate, subscription is not able to be
		// payed
		boolean isPreviousToMinimalDate = today.isBefore(minimalPayDate);

		return !isPreviousToMinimalDate;
	}

	public PaymentsResponseDTO getPaymentsOfUser(Long userId) {
		List<PaymentInfoDTO> payments = this.repository.findAllBySuscripcionUsuarioProveedorId(userId).stream()
				.map(payment -> new PaymentInfoDTO(payment.getId(), payment.getExternalId(), payment.getPaymentPeriod(),
						payment.getDate(), payment.getAmount(), payment.getCurrency(), payment.getState().toString(),
						payment.getPaymentProvider().getName()))
				.collect(Collectors.toList());
				
		Map<String, String> states = new HashMap<>();
		
		this.paymentStateRepository.findAll().forEach(paymentState -> {
			states.put(paymentState.getState(), paymentState.getDescription());
		});
		
		return new PaymentsResponseDTO(states, payments);
	}
}
