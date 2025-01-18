package com.contractar.microserviciopayment.services;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.contractar.microserviciocommons.constants.controllers.ProveedorControllerUrls;
import com.contractar.microserviciocommons.constants.controllers.SecurityControllerUrls;
import com.contractar.microserviciocommons.dto.SuscripcionDTO;
import com.contractar.microserviciocommons.exceptions.payment.PaymentAlreadyDone;
import com.contractar.microserviciocommons.exceptions.proveedores.SuscriptionNotFound;
import com.contractar.microserviciopayment.dtos.AuthResponse;
import com.contractar.microserviciopayment.dtos.PaymentCreateDTO;
import com.contractar.microserviciopayment.models.OutsitePaymentProviderImpl;
import com.contractar.microserviciopayment.models.Payment;
import com.contractar.microserviciopayment.models.PaymentProvider;
import com.contractar.microserviciopayment.models.SuscriptionPayment;
import com.contractar.microserviciopayment.models.enums.IntegrationType;
import com.contractar.microserviciopayment.providers.PaymentUrls;
import com.contractar.microserviciopayment.providers.uala.WebhookBody;
import com.contractar.microserviciopayment.repository.OutsitePaymentProviderRepository;
import com.contractar.microserviciopayment.repository.PaymentProviderRepository;
import com.contractar.microserviciopayment.repository.PaymentRepository;
import com.contractar.microserviciopayment.repository.SuscriptionPaymentRepository;

import jakarta.transaction.Transactional;

@Service
public class PaymentService {

	private PaymentProviderRepository paymentProviderRepository;

	private OutsitePaymentProviderRepository outsitePaymentProviderRepository;

	private PaymentRepository paymentRepository;

	private SuscriptionPaymentRepository suscriptionPaymentRepository;

	private SuscriptionPaymentService suscriptionPaymentService;

	private RestTemplate httpClient;

	private ProviderServiceImplFactory providerServiceImplFactory;

	@Value("${microservicio-config.url}")
	private String configServiceUrl;

	@Value("${frontend.payment.signup.suscription}")
	private String frontendReturnUrl;

	@Value("${provider.uala.webhookUrl}")
	private String webhookUrl;

	@Value("${microservicio-security.url}")
	private String serviceSecurityUrl;

	@Value("${microservicio-usuario.url}")
	private String microservicioUsuarioUrl;
	
	private static int PAYMENT_URL_MINUTES_DURATION;

	public PaymentService(OutsitePaymentProviderRepository outsitePaymentProviderRepository,
			PaymentProviderRepository paymentProviderRepository, PaymentRepository paymentRepository,
			ProviderServiceImplFactory providerServiceImplFactory,
			SuscriptionPaymentRepository suscriptionPaymentRepository, RestTemplate httpClient,
			SuscriptionPaymentService suscriptionPaymentService) {
		this.providerServiceImplFactory = providerServiceImplFactory;
		this.httpClient = httpClient;
		this.paymentProviderRepository = paymentProviderRepository;
		this.outsitePaymentProviderRepository = outsitePaymentProviderRepository;
		this.suscriptionPaymentRepository = suscriptionPaymentRepository;
		this.paymentRepository = paymentRepository;
		this.suscriptionPaymentService = suscriptionPaymentService;
	}

	private String getMessageTag(String tagId) {
		final String fullUrl = configServiceUrl + "/i18n/" + tagId;
		return httpClient.getForObject(fullUrl, String.class);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private boolean checkUserToken(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);

		UriComponentsBuilder tokenCheckUrlBuilder = UriComponentsBuilder.fromHttpUrl(serviceSecurityUrl)
				.path(SecurityControllerUrls.GET_USER_PAYLOAD_FROM_TOKEN);

		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<Map> response = httpClient.exchange(tokenCheckUrlBuilder.toUriString(), HttpMethod.GET, entity,
				Map.class);

		Map<String, Object> payload = response.getBody();
		
		long expiresField = ((Number) payload.get("exp")).longValue();

		LocalDateTime dateTimeFromUnix = Instant.ofEpochSecond(expiresField).atZone(ZoneId.systemDefault())
				.toLocalDateTime();

		LocalDate today = LocalDate.now();

		LocalDate dateFromUnix = dateTimeFromUnix.toLocalDate();

		return !dateFromUnix.isBefore(today);

	}

	public PaymentProvider getActivePaymentProvider() {
		// Only one payment provider can be active
		// TODO: raise exceptions
		List<PaymentProvider> paymentProviders = paymentProviderRepository.findByIsActiveTrue();
		if (paymentProviders.isEmpty()) {
			return null;
		}

		IntegrationType providerIntegration = paymentProviders.get(0).getIntegrationType();

		if (providerIntegration.equals(IntegrationType.OUTSITE)) {
			return outsitePaymentProviderRepository.findById(paymentProviders.get(0).getId()).map(p -> p).orElse(null);
		}

		return null;
	}

	private SuscripcionDTO getSuscription(Long suscripcionId) throws SuscriptionNotFound {
		try {
			String url = microservicioUsuarioUrl
					+ ProveedorControllerUrls.GET_SUSCRIPCION.replace("{suscriptionId}", suscripcionId.toString());
			return httpClient.getForObject(url, SuscripcionDTO.class);
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode().equals(HttpStatusCode.valueOf(404))) {
				throw new SuscriptionNotFound(getMessageTag("exception.suscription.notFound"));
			}

			throw e;
		}
	}

	public SuscriptionPayment findLastSuscriptionPayment(Long suscriptionId) {
		return suscriptionPaymentRepository.findTopBySuscripcionIdOrderByDateDesc(suscriptionId).map(payment -> payment)
				.orElse(null);
	}

	public Payment createPayment(PaymentCreateDTO dto) {
		PaymentProvider currentProvider = this.getActivePaymentProvider();

		if (currentProvider == null) {
			throw new RuntimeException("Payment provider not set");
		}

		if (!currentProvider.getIntegrationType().equals(IntegrationType.OUTSITE)) {
			throw new RuntimeException("Invalid integration type");
		}

		com.contractar.microserviciopayment.providers.OutsitePaymentProvider paymentProviderImpl = providerServiceImplFactory
				.getOutsitePaymentProvider();

		Payment newPayment = new Payment(dto.getExternalId(), dto.getPaymentPeriod(), LocalDate.now(), dto.getAmount(),
				dto.getCurrency(), currentProvider, null);

		paymentProviderImpl.setPaymentAsPending(newPayment);
		return paymentRepository.save(newPayment);
	}

	/**
	 * 
	 * @param suscriptionId
	 * @param amount
	 * @return The checkout url to be used by the frontend so the user can finish
	 *         the pay there
	 * @throws SuscriptionNotFound
	 * @throws PaymentAlreadyDone
	 */
	@Transactional
	public String payLastSuscriptionPeriod(Long suscriptionId) throws SuscriptionNotFound, PaymentAlreadyDone {
		PaymentProvider currentProvider = this.getActivePaymentProvider();

		SuscripcionDTO foundSuscription = this.getSuscription(suscriptionId);

		// TODO: receive by param the integration type. Depending on that, fetch the
		// proper payment provider configuration and use the required
		// provider services/entities, etc.
		OutsitePaymentProviderImpl activePaymentProvider = (OutsitePaymentProviderImpl) currentProvider;
		String authToken = activePaymentProvider.getToken();

		com.contractar.microserviciopayment.providers.OutsitePaymentProvider paymentProviderImpl = providerServiceImplFactory
				.getOutsitePaymentProvider();

		if (!suscriptionPaymentService.canSuscriptionBePayed(suscriptionId, paymentProviderImpl)) {
			return "";
		}

		SuscriptionPayment lastPayment = this.findLastSuscriptionPayment(suscriptionId);
		
		if (lastPayment != null && paymentProviderImpl.isPaymentPending(lastPayment)) {
			boolean isLinkValid = Duration.between(lastPayment.getLinkCreationTime(), LocalDateTime.now()).toMinutes() <= PAYMENT_URL_MINUTES_DURATION;
			if (isLinkValid) {
				return lastPayment.getPaymentUrl();
			}
		}

		YearMonth lastPeriodPayment = Optional.ofNullable(lastPayment).map(payment -> payment.getPaymentPeriod())
				.orElse(null);

		YearMonth paymentPeriod = lastPeriodPayment != null ? lastPeriodPayment.plusMonths(1) : YearMonth.now();

		LocalDate today = LocalDate.now();

		LocalDate paymentDate;

		if (lastPayment == null) {
			paymentDate = today;
		} else {
			LocalDate newProbablePaymentDate = lastPayment.getDate().plusMonths(1);

			if (newProbablePaymentDate.isAfter(today)) {
				paymentDate = newProbablePaymentDate;
			} else {
				paymentDate = today;
			}
		}

		PaymentCreateDTO paymentCreateDTO = new PaymentCreateDTO(null, paymentPeriod, foundSuscription.getPlanPrice(),
				Currency.getInstance("ARS"), suscriptionId, paymentDate);

		if (!StringUtils.hasLength(authToken) || !checkUserToken(authToken)) {
			AuthResponse authResponse = (AuthResponse) paymentProviderImpl.auth();
			authToken = authResponse.getAccessToken();
			activePaymentProvider.setToken(authToken);
			paymentProviderImpl.save(activePaymentProvider);
		}

		SuscriptionPayment createdPayment = suscriptionPaymentService.createPayment(paymentCreateDTO,
				activePaymentProvider, suscriptionId);

		String createdPaymentId = createdPayment.getId().toString();

		String successReturnUrl = frontendReturnUrl.replace("{paymentResult}", "success").replace("{paymentId}",
				createdPaymentId);

		String errorReturnUrl = frontendReturnUrl.replace("{paymentResult}", "error").replace("{paymentId}",
				createdPaymentId);

		String notificationUrl = webhookUrl.replace("{paymentResult}", "error").replace("{paymentId}",
				createdPaymentId);

		PaymentUrls urls = new PaymentUrls(successReturnUrl, errorReturnUrl, notificationUrl);

		String checkoutUrl = paymentProviderImpl.createCheckout(foundSuscription.getPlanPrice(),
				getMessageTag("payment.suscription.description"), createdPayment.getId(), urls, authToken);
		
		createdPayment.setPaymentUrl(checkoutUrl);
		
		createdPayment.setLinkCreationTime(LocalDateTime.now());
		
		return checkoutUrl;

	}

	public void handleWebhookNotification(WebhookBody body) {
		com.contractar.microserviciopayment.providers.OutsitePaymentProvider paymentProviderImpl = providerServiceImplFactory
				.getOutsitePaymentProvider();
		paymentProviderImpl.handleWebhookNotification(body);
	}

}
