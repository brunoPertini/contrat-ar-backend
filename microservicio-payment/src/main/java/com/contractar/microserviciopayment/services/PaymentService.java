package com.contractar.microserviciopayment.services;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.contractar.microserviciocommons.constants.controllers.SecurityControllerUrls;
import com.contractar.microserviciopayment.dtos.AuthResponse;
import com.contractar.microserviciopayment.dtos.AuthTokenResponse;
import com.contractar.microserviciopayment.models.OutsitePaymentProvider;
import com.contractar.microserviciopayment.models.PaymentProvider;
import com.contractar.microserviciopayment.providers.uala.Uala;
import com.contractar.microserviciopayment.providers.uala.UalaAuthResponse;

@Service
public class PaymentService {

	private PaymentProvider activePaymentProvider;

	private Uala ualaPaymentProviderService;

	private RestTemplate httpClient;

	@Value("${microservicio-config.url}")
	private String configServiceUrl;
	
	@Value("${frontend.payment.signup.suscription}")
	private String frontendReturnUrl;
	
	@Value("${microservicio-security.url}")
	private String serviceSecurityUrl;

	public PaymentService(PaymentProvider activePaymentProvider, Uala ualaPaymentProviderService,
			RestTemplate httpClient) {
		this.activePaymentProvider = activePaymentProvider;
		this.ualaPaymentProviderService = ualaPaymentProviderService;
		this.httpClient = httpClient;
	}

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
	
	private boolean checkUserToken(String token) {
		UriComponentsBuilder tokenCheckUrlBuilder = UriComponentsBuilder.fromHttpUrl(serviceSecurityUrl)
				.path(SecurityControllerUrls.TOKEN_BASE_PATH).queryParam("token", token);

		return httpClient.getForObject(tokenCheckUrlBuilder.toUriString(), Boolean.class);
	}

	/**
	 * 
	 * @param suscriptionId
	 * @param amount
	 * @return The checkout url to be used by the frontend so the user can finish
	 *         the pay there
	 */
	public String payLastSuscriptionPeriod(Long suscriptionId, int amount) {
		OutsitePaymentProvider castedPaymentProvider = (OutsitePaymentProvider) activePaymentProvider;
		Optional<String> tokenOpt = Optional.ofNullable(castedPaymentProvider.getToken());
		
		String authToken;
		
		com.contractar.microserviciopayment.providers.OutsitePaymentProvider paymentProviderImpl = createOutsitePaymentProvider(
				((PaymentProvider) activePaymentProvider).getId());

		if (!tokenOpt.isPresent() || !StringUtils.hasLength(tokenOpt.get())) {
			AuthResponse  authResponse = (AuthResponse) paymentProviderImpl.auth();
			castedPaymentProvider.setToken(authResponse.getAccessToken());
			paymentProviderImpl.save(castedPaymentProvider);
		} else {
			authToken = tokenOpt.get();
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
