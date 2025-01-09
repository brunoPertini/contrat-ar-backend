package com.contractar.microserviciopayment.services;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.contractar.microserviciocommons.constants.controllers.ProveedorControllerUrls;
import com.contractar.microserviciocommons.constants.controllers.SecurityControllerUrls;
import com.contractar.microserviciocommons.dto.SuscripcionDTO;
import com.contractar.microserviciocommons.exceptions.proveedores.SuscriptionNotFound;
import com.contractar.microserviciopayment.dtos.AuthResponse;
import com.contractar.microserviciopayment.dtos.PaymentCreateDTO;
import com.contractar.microserviciopayment.models.OutsitePaymentProviderImpl;
import com.contractar.microserviciopayment.models.Payment;
import com.contractar.microserviciopayment.models.PaymentProvider;
import com.contractar.microserviciopayment.models.SuscriptionPayment;
import com.contractar.microserviciopayment.models.enums.IntegrationType;
import com.contractar.microserviciopayment.providers.uala.Uala;
import com.contractar.microserviciopayment.repository.OutsitePaymentProviderRepository;
import com.contractar.microserviciopayment.repository.PaymentProviderRepository;
import com.contractar.microserviciopayment.repository.PaymentRepository;
import com.contractar.microserviciopayment.repository.SuscriptionPaymentRepository;

@Service
public class PaymentService {
	
	private PaymentProviderRepository paymentProviderRepository;
	
	private OutsitePaymentProviderRepository outsitePaymentProviderRepository;
	
	private PaymentRepository paymentRepository;
	
	private SuscriptionPaymentRepository suscriptionPaymentRepository;

	private Uala ualaPaymentProviderService;

	private RestTemplate httpClient;
	
	private final PaymentProvider currentProvider;

	@Value("${microservicio-config.url}")
	private String configServiceUrl;

	@Value("${frontend.payment.signup.suscription}")
	private String frontendReturnUrl;

	@Value("${microservicio-security.url}")
	private String serviceSecurityUrl;
	
	@Value("${microservicio-usuario.url}")
	private String microservicioUsuarioUrl;

	public PaymentService(OutsitePaymentProviderRepository outsitePaymentProviderRepository, 
			PaymentProviderRepository paymentProviderRepository,
			PaymentRepository paymentRepository,
			Uala ualaPaymentProviderService,
			SuscriptionPaymentRepository suscriptionPaymentRepository,
			RestTemplate httpClient) {
		this.ualaPaymentProviderService = ualaPaymentProviderService;
		this.httpClient = httpClient;
		this.paymentProviderRepository = paymentProviderRepository;
		this.outsitePaymentProviderRepository = outsitePaymentProviderRepository;
		this.suscriptionPaymentRepository = suscriptionPaymentRepository;
		this.paymentRepository = paymentRepository;
		this.currentProvider = this.getActivePaymentProvider();
	}

	@SuppressWarnings("rawtypes")
	private com.contractar.microserviciopayment.providers.OutsitePaymentProvider createOutsitePaymentProvider(
			Long providerId) {
		Map<Long, com.contractar.microserviciopayment.providers.OutsitePaymentProvider> creators = Map.of(1L,
				ualaPaymentProviderService);

		return creators.get(providerId);
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
		
		ResponseEntity<Map> response = httpClient.exchange(tokenCheckUrlBuilder.toUriString(),
				HttpMethod.GET,
				entity,
				Map.class);

		Map<String, Object> payload = response.getBody();

		int expiresField = (int) payload.get("exp");

		LocalDateTime dateTimeFromUnix = Instant.ofEpochSecond(expiresField).atZone(ZoneId.systemDefault())
				.toLocalDateTime();

		LocalDate today = LocalDate.now();

		LocalDate dateFromUnix = dateTimeFromUnix.toLocalDate();

		return !dateFromUnix.isBefore(today);

	}
	
	private PaymentProvider getActivePaymentProvider() {
		// Only one payment provider can be active
		// TODO: raise exceptions
		List<PaymentProvider> paymentProviders = paymentProviderRepository.findByIsActiveTrue();
		if (paymentProviders.isEmpty()) {
			return null;
		}
		
		IntegrationType providerIntegration = paymentProviders.get(0).getIntegrationType();

		if (providerIntegration.equals(IntegrationType.OUTSITE)) {
			return outsitePaymentProviderRepository.findById(paymentProviders.get(0).getId())
					.map(p -> p)
					.orElse(null);
		}
		
		return null;
	}
	
	private SuscripcionDTO getSuscription(Long suscripcionId) throws SuscriptionNotFound {
		String url = microservicioUsuarioUrl + ProveedorControllerUrls.GET_SUSCRIPCION.replace("{suscriptionId}", suscripcionId.toString());
		return httpClient.getForObject(url, SuscripcionDTO.class);
	}
	
	public SuscriptionPayment findLastSuscriptionPayment(Long suscriptionId) {
		 return suscriptionPaymentRepository.findTopBySuscripcionIdOrderByDateDesc(suscriptionId)
			        .map(payment -> payment)
			        .orElse(null);
	}
	
	public Payment createPayment(PaymentCreateDTO dto) {
		if (this.currentProvider == null) {
			throw new RuntimeException("Payment provider not set");
		}
		
	    if (!currentProvider.getIntegrationType().equals(IntegrationType.OUTSITE)) {
	        throw new RuntimeException("Invalid integration type");
	    }

	    com.contractar.microserviciopayment.providers.OutsitePaymentProvider paymentProviderImpl = 
	        this.createOutsitePaymentProvider(currentProvider.getId());

	    Payment newPayment = new Payment(dto.getExternalId(), 
	            dto.getPaymentPeriod(), 
	            LocalDate.now(),
	            dto.getAmount(),
	            dto.getCurrency(), 
	            currentProvider,
	            null);

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
	 */
	public String payLastSuscriptionPeriod(Long suscriptionId) throws SuscriptionNotFound {
		SuscripcionDTO foundSuscription = Optional.ofNullable(this.getSuscription(suscriptionId)).map(s -> s)
				.orElseThrow(() -> new SuscriptionNotFound(getMessageTag("exception.suscription.notFound")));

		// TODO: receive by param the integration type. Depending on that, fetch the proper payment provider configuration and use the required
		// provider services/entities, etc.
		OutsitePaymentProviderImpl activePaymentProvider = (OutsitePaymentProviderImpl) this.currentProvider;
		String authToken = activePaymentProvider.getToken();
		
		com.contractar.microserviciopayment.providers.OutsitePaymentProvider paymentProviderImpl = createOutsitePaymentProvider(activePaymentProvider.getId());

		if (!StringUtils.hasLength(authToken) || !checkUserToken(authToken)) {
			AuthResponse  authResponse = (AuthResponse) paymentProviderImpl.auth();
			authToken = authResponse.getAccessToken();
			activePaymentProvider.setToken(authToken);
			paymentProviderImpl.save(activePaymentProvider);
		} 
		
		
		YearMonth lastPeriodPayment = Optional.ofNullable(this.findLastSuscriptionPayment(suscriptionId))
				.map(payment -> payment.getPaymentPeriod())
				.orElse(null);
		
		YearMonth paymentPeriod = lastPeriodPayment != null ? lastPeriodPayment.plusMonths(1) : YearMonth.now();
		
		PaymentCreateDTO paymentCreateDTO = new PaymentCreateDTO(null,
				paymentPeriod,
				foundSuscription.getPlanPrice(),
				Currency.getInstance("ARS"),
				suscriptionId);
		
		Payment createdPayment = this.createPayment(paymentCreateDTO);
		
		String  createdPaymentId = createdPayment.getId().toString();
		
		String successReturnUrl = frontendReturnUrl.replace("{paymentResult}", "success").replace("{paymentId}", createdPaymentId);
		
		String errorReturnUrl = frontendReturnUrl.replace("{paymentResult}", "error").replace("{paymentId}", createdPaymentId);
		
		PaymentUrls urls = new PaymentUrls(successReturnUrl, errorReturnUrl, null);
		
		return paymentProviderImpl.createCheckout(foundSuscription.getPlanPrice(), getMessageTag("payment.suscription.description"), urls, authToken);

	}

	public class PaymentUrls {
		private String successUrl;
		private String failUrl;
		private String notificationsUrl;

		public PaymentUrls() {
		}

		public PaymentUrls(String successUrl, String failUrl, String notificationsUrl) {
			this.successUrl = successUrl;
			this.failUrl = failUrl;
			this.notificationsUrl = notificationsUrl;
		}

		public String getSuccessUrl() {
			return successUrl;
		}

		public void setSuccessUrl(String successUrl) {
			this.successUrl = successUrl;
		}

		public String getFailUrl() {
			return failUrl;
		}

		public void setFailUrl(String failUrl) {
			this.failUrl = failUrl;
		}

		public String getNotificationsUrl() {
			return notificationsUrl;
		}

		public void setNotificationsUrl(String notificationsUrl) {
			this.notificationsUrl = notificationsUrl;
		}
	}

}
