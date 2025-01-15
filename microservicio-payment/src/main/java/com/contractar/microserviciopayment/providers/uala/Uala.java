package com.contractar.microserviciopayment.providers.uala;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciopayment.models.OutsitePaymentProviderImpl;
import com.contractar.microserviciopayment.models.Payment;
import com.contractar.microserviciopayment.models.UalaPaymentState;
import com.contractar.microserviciopayment.models.enums.UalaPaymentStateValue;
import com.contractar.microserviciopayment.providers.OutsitePaymentProvider;
import com.contractar.microserviciopayment.providers.PaymentUrls;
import com.contractar.microserviciopayment.repository.OutsitePaymentProviderRepository;
import com.contractar.microserviciopayment.repository.PaymentRepository;
import com.contractar.microserviciopayment.repository.UalaPaymentStateRepository;

import jakarta.transaction.Transactional;

@Component
public class Uala implements OutsitePaymentProvider<CheckoutBody, OutsitePaymentProviderImpl, UalaAuthResponse, WebhookBody> {
	private static final String keysPrefix = "provider.uala.prod";

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

	private OutsitePaymentProviderRepository ualaPaymentProviderRepository;
	
	private UalaPaymentStateRepository ualaPaymentStateRepository;
	
	private PaymentRepository paymentRepository;

	public Uala(RestTemplate httpClient,
			OutsitePaymentProviderRepository ualaPaymentProviderRepository,
			UalaPaymentStateRepository ualaPaymentStateRepository,
			PaymentRepository paymentRepository) {
		this.httpClient = httpClient;
		this.ualaPaymentProviderRepository = ualaPaymentProviderRepository;
		this.ualaPaymentStateRepository = ualaPaymentStateRepository;
		this.paymentRepository = paymentRepository;
	}
	
	@Transactional
	public void handleWebhookNotification(WebhookBody body) {
		Long paymentId = Long.valueOf(body.getExternalReference());
		paymentRepository.findById(paymentId).ifPresent(payment -> {
		    UalaPaymentState newState = ualaPaymentStateRepository.findByState(body.getStatus()).get();
		    
		    payment.setState(newState);
		    payment.setExternalId(body.getUuid());
		    
		    paymentRepository.save(payment);
		});

	}

	@SuppressWarnings("unchecked")
	@Override
	public UalaAuthResponse auth() {
		AuthBody authBody = new AuthBody(username, clientId, clientSecretId);

		return httpClient.postForObject(authUrl, authBody, UalaAuthResponse.class);

	}

	@SuppressWarnings("unchecked")
	@Override
	public String createCheckout(int amount, String description, Long externalReference, PaymentUrls urls, String authToken) {
		CheckoutBody checkoutBody = this.createCheckoutBody(amount, description, urls.getFailUrl(),
				urls.getSuccessUrl(), urls.getNotificationUrl(), externalReference);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + authToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<?> entity = new HttpEntity<>(checkoutBody, headers);

		Map<String, Object> response = httpClient.postForObject(checkoutUrl, entity, Map.class);
		Map<String, String> links = (Map<String, String>) response.get("links");
		return links.get("checkout_link");

	}

	@Override
	public OutsitePaymentProviderImpl save(OutsitePaymentProviderImpl entity) {
		return ualaPaymentProviderRepository.save(entity);
	}

	@Override
	public CheckoutBody createCheckoutBody(int amount, String description, String callbackFail, String callbackSuccess,
			String notificationUrl, Long externalReference) {
		return new CheckoutBody(amount, description, username, callbackFail, callbackSuccess, notificationUrl, externalReference.toString());
	}

	@Override
	public void setPaymentAsPending(Payment p) {
		UalaPaymentState pendingState = ualaPaymentStateRepository.findByState(UalaPaymentStateValue.PENDING).get();
		p.setState(pendingState);
	}

	@Override
	public boolean wasPaymentAccepted(Payment payment) {
		return ((UalaPaymentState) payment.getState()).getState().equals(UalaPaymentStateValue.APPROVED);
	}
	
	@Override
	public boolean wasPaymentRejected(Payment payment) {
		return ((UalaPaymentState) payment.getState()).getState().equals(UalaPaymentStateValue.REJECTED);
	}

	@Override
	public boolean isPaymentPending(Payment payment) {
		UalaPaymentStateValue stateValue = ((UalaPaymentState) payment.getState()).getState();
		return stateValue.equals(UalaPaymentStateValue.PENDING) || stateValue.equals(UalaPaymentStateValue.PROCESSED);
	}

}
