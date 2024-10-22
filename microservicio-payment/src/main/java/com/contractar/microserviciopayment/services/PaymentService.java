package com.contractar.microserviciopayment.services;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.contractar.microserviciocommons.constants.controllers.SecurityControllerUrls;
import com.contractar.microserviciopayment.dtos.AuthResponse;
import com.contractar.microserviciopayment.models.OutsitePaymentProviderImpl;
import com.contractar.microserviciopayment.models.PaymentProvider;
import com.contractar.microserviciopayment.models.enums.IntegrationType;
import com.contractar.microserviciopayment.providers.uala.Uala;
import com.contractar.microserviciopayment.repository.OutsitePaymentProviderRepository;
import com.contractar.microserviciopayment.repository.PaymentProviderRepository;

@Service
public class PaymentService {
	
	private PaymentProviderRepository paymentProviderRepository;
	
	private OutsitePaymentProviderRepository outsitePaymentProviderRepository;

	private Uala ualaPaymentProviderService;

	private RestTemplate httpClient;

	@Value("${microservicio-config.url}")
	private String configServiceUrl;

	@Value("${frontend.payment.signup.suscription}")
	private String frontendReturnUrl;

	@Value("${microservicio-security.url}")
	private String serviceSecurityUrl;

	public PaymentService(OutsitePaymentProviderRepository outsitePaymentProviderRepository, 
			PaymentProviderRepository paymentProviderRepository, 
			Uala ualaPaymentProviderService,
			RestTemplate httpClient) {
		this.ualaPaymentProviderService = ualaPaymentProviderService;
		this.httpClient = httpClient;
		this.paymentProviderRepository = paymentProviderRepository;
		this.outsitePaymentProviderRepository = outsitePaymentProviderRepository;
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

	/**
	 * 
	 * @param suscriptionId
	 * @param amount
	 * @return The checkout url to be used by the frontend so the user can finish
	 *         the pay there
	 */
	public String payLastSuscriptionPeriod(Long suscriptionId, int amount) {
		OutsitePaymentProviderImpl activePaymentProvider = (OutsitePaymentProviderImpl) this.getActivePaymentProvider();
		String authToken = activePaymentProvider.getToken();
		
		com.contractar.microserviciopayment.providers.OutsitePaymentProvider paymentProviderImpl = createOutsitePaymentProvider(activePaymentProvider.getId());

		if (!StringUtils.hasLength(authToken) || !checkUserToken(authToken)) {
			AuthResponse  authResponse = (AuthResponse) paymentProviderImpl.auth();
			authToken = authResponse.getAccessToken();
			activePaymentProvider.setToken(authToken);
			paymentProviderImpl.save(activePaymentProvider);
		} 
		
		String successReturnUrl = frontendReturnUrl.replace("{paymentResult}", "success").replace("{paymentId}", "1");
		
		String errorReturnUrl = frontendReturnUrl.replace("{paymentResult}", "error").replace("{paymentId}", "1");
		
		PaymentUrls urls = new PaymentUrls(successReturnUrl, errorReturnUrl, null);
		
		return paymentProviderImpl.createCheckout(amount, getMessageTag("payment.suscription.description"), urls, authToken);

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
