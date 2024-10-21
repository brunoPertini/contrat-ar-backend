package com.contractar.microserviciopayment.providers.uala;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciopayment.models.UalaPaymentProvider;
import com.contractar.microserviciopayment.providers.OutsitePaymentProvider;
import com.contractar.microserviciopayment.repository.UalaPaymentProviderRepository;
import com.contractar.microserviciopayment.services.PaymentService.PaymentUrls;

@Component
public class Uala implements OutsitePaymentProvider<AuthBody, CheckoutBody, UalaPaymentProvider, UalaAuthResponse> {
	private static final String keysPrefix = "provider.uala";

	@Value("${" + keysPrefix + ".username}")
	private String username;

	@Value("${" + keysPrefix + ".clientId}")
	private String clientId;

	@Value("${" + keysPrefix + ".clientSecretId}")
	private String clientSecretId;

	@Value("${" + keysPrefix + ".authUrl}")
	private String authUrl;

	@Value("${" + keysPrefix + ".checkoutUrl}")
	private String checkoutUrl;

	private RestTemplate httpClient;

	private UalaPaymentProviderRepository ualaPaymentProviderRepository;

	public Uala(RestTemplate httpClient, UalaPaymentProviderRepository ualaPaymentProviderRepository) {
		this.httpClient = httpClient;
		this.ualaPaymentProviderRepository = ualaPaymentProviderRepository;
	}

	@SuppressWarnings("unchecked")
	@Override
	public UalaAuthResponse auth() {
		AuthBody authBody = new AuthBody(username, clientId, clientSecretId);

		return httpClient.postForObject(authUrl, authBody, UalaAuthResponse.class);

	}

	@SuppressWarnings("unchecked")
	@Override
	public String createCheckout(int amount, String description, PaymentUrls urls, String authToken) {
		CheckoutBody checkoutBody = this.createCheckoutBody(amount, description, urls.getFailUrl(),
				urls.getSuccessUrl(), urls.getNotificationsUrl());

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + authToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<?> entity = new HttpEntity<>(checkoutBody, headers);

		Map<String, Object> response = httpClient.postForObject(checkoutUrl, entity, Map.class);
		Map<String, String> links = (Map<String, String>) response.get("links");
		return links.get("checkout_link");

	}

	@Override
	public UalaPaymentProvider save(UalaPaymentProvider entity) {
		return ualaPaymentProviderRepository.save(entity);
	}

	@Override
	public CheckoutBody createCheckoutBody(int amount, String description, String callbackFail, String callbackSuccess,
			String notificationUrl) {
		return new CheckoutBody(amount, description, username, callbackFail, callbackSuccess, null);
	}

}
