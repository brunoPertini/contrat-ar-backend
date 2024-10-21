package com.contractar.microserviciopayment.services;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciopayment.models.OutsitePaymentProvider;
import com.contractar.microserviciopayment.models.PaymentProvider;
import com.contractar.microserviciopayment.providers.uala.Uala;

@Service
public class PaymentService {

	private OutsitePaymentProvider activePaymentProvider;

	private Uala ualaPaymentProviderService;

	private RestTemplate httpClient;

	@Value("${microservicio-config.url}")
	private String configServiceUrl;
	
	@Value("${frontend.payment.signup.suscription}")
	private String frontendReturnUrl;

	public PaymentService(OutsitePaymentProvider activePaymentProvider, Uala ualaPaymentProviderService,
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

	/**
	 * 
	 * @param suscriptionId
	 * @param amount
	 * @return The checkout url to be used by the frontend so the user can finish
	 *         the pay there
	 */
	public String payLastSuscriptionPeriod(Long suscriptionId, int amount) {
		Optional<String> tokenOpt = Optional.ofNullable(activePaymentProvider.getToken());
		
		String authToken;
		
		com.contractar.microserviciopayment.providers.OutsitePaymentProvider paymentProviderImpl = createOutsitePaymentProvider(
				((PaymentProvider) activePaymentProvider).getId());

		if (!tokenOpt.isPresent() || !StringUtils.hasLength(tokenOpt.get())) {
			authToken = paymentProviderImpl.auth();
			activePaymentProvider.setToken(authToken);
			paymentProviderImpl.save(activePaymentProvider);
		} else {
			authToken = tokenOpt.get();
		}
		
		
		String successReturnUrl = frontendReturnUrl.replace("{paymentResult}", "success");
		
		String errorReturnUrl = frontendReturnUrl.replace("{paymentResult}", "error");
		
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
