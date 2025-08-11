package com.contractar.microserviciopayment.providers.uala;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciocommons.constants.controllers.PromotionControllerUrls;
import com.contractar.microserviciocommons.constants.controllers.ProveedorControllerUrls;
import com.contractar.microserviciocommons.dto.SuscriptionActiveUpdateDTO;
import com.contractar.microserviciocommons.dto.usuario.PromotionInstanceUpdateDTO;
import com.contractar.microserviciopayment.models.OutsitePaymentProviderImpl;
import com.contractar.microserviciopayment.models.Payment;
import com.contractar.microserviciopayment.models.UalaPaymentState;
import com.contractar.microserviciopayment.models.enums.UalaPaymentStateValue;
import com.contractar.microserviciopayment.providers.OutsitePaymentProvider;
import com.contractar.microserviciopayment.providers.PaymentUrls;
import com.contractar.microserviciopayment.repository.OutsitePaymentProviderRepository;
import com.contractar.microserviciopayment.repository.PaymentRepository;
import com.contractar.microserviciopayment.repository.SuscriptionPaymentRepository;
import com.contractar.microserviciopayment.repository.UalaPaymentStateRepository;

import jakarta.transaction.Transactional;

@Component
public class Uala
		implements OutsitePaymentProvider<CheckoutBody, OutsitePaymentProviderImpl, UalaAuthResponse, WebhookBody> {
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

	@Value("${microservicio-usuario.url}")
	private String usersServiceUrl;

	private RestTemplate httpClient;

	private OutsitePaymentProviderRepository ualaPaymentProviderRepository;

	private UalaPaymentStateRepository ualaPaymentStateRepository;

	private PaymentRepository paymentRepository;

	private SuscriptionPaymentRepository suscriptionPaymentRepository;

	public Uala(RestTemplate httpClient, OutsitePaymentProviderRepository ualaPaymentProviderRepository,
			UalaPaymentStateRepository ualaPaymentStateRepository, PaymentRepository paymentRepository,
			SuscriptionPaymentRepository suscriptionPaymentRepository) {
		this.httpClient = httpClient;
		this.ualaPaymentProviderRepository = ualaPaymentProviderRepository;
		this.ualaPaymentStateRepository = ualaPaymentStateRepository;
		this.paymentRepository = paymentRepository;
		this.suscriptionPaymentRepository = suscriptionPaymentRepository;
	}

	@Transactional
	public void handleWebhookNotification(WebhookBody body) {
		Long paymentId = Long.valueOf(body.getExternalReference());
		suscriptionPaymentRepository.findById(paymentId).ifPresent(payment -> {
			UalaPaymentState newState = ualaPaymentStateRepository.findByState(body.getStatus().name()).get();

			payment.setState(newState);
			payment.setExternalId(body.getUuid());

			paymentRepository.save(payment);
			
			// TODO: extract this either to async or message queue
			Optional.ofNullable(payment.getPromotionId()).ifPresent(promotionId -> {
				Long subscriptionId = payment.getSuscripcion().getId();

				String updatePromotionUrl = usersServiceUrl + PromotionControllerUrls.PROMOTION_BASE_URL 
						+ PromotionControllerUrls.PROMOTION_INSTANCE_FULL_URL
						.replace("{suscriptionId}", subscriptionId.toString())
						.replace("{promotionId}", promotionId.toString());
				
				httpClient.put(updatePromotionUrl, new PromotionInstanceUpdateDTO(LocalDate.now().minusDays(1)));
			});
			
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
	public String createCheckout(int amount, String description, Long externalReference, PaymentUrls urls,
			String authToken) {
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
		return new CheckoutBody(amount, description, username, callbackFail, callbackSuccess, notificationUrl,
				externalReference.toString());
	}

	@Override
	public void setPaymentAsPending(Payment p) {
		UalaPaymentState pendingState = ualaPaymentStateRepository.findByState(UalaPaymentStateValue.PENDING.name()).get();
		p.setState(pendingState);
	}

	@Override
	public boolean wasPaymentAccepted(Payment payment) {
		return payment.getState().getState().equals(UalaPaymentStateValue.APPROVED.name());
	}

	@Override
	public boolean wasPaymentRejected(Payment payment) {
		return payment.getState().getState().equals(UalaPaymentStateValue.REJECTED.name());
	}

	@Override
	public boolean isPaymentPending(Payment payment) {
		UalaPaymentStateValue stateValue = UalaPaymentStateValue.valueOf(payment.getState().getState());
		return stateValue.equals(UalaPaymentStateValue.PENDING);
	}

	@Override
	public boolean isPaymentProcessed(Payment payment) {
		UalaPaymentStateValue stateValue = UalaPaymentStateValue.valueOf(payment.getState().getState());
		return stateValue.equals(UalaPaymentStateValue.PROCESSED);
	}

	@Override
	public String getSuccessStateValue() {
		return UalaPaymentStateValue.APPROVED.toString();
	}

	@Override
	public String getFailureStateValue() {
		return UalaPaymentStateValue.REJECTED.toString();
	}

	@Override
	public void handleWebhookPlanChangeNotification(WebhookBody body) {
		Long paymentId = Long.valueOf(body.getExternalReference());
		suscriptionPaymentRepository.findById(paymentId).ifPresent(payment -> {
			UalaPaymentState newState = ualaPaymentStateRepository.findByState(body.getStatus().name()).get();
			payment.setState(newState);
			payment.setExternalId(body.getUuid());
			suscriptionPaymentRepository.save(payment);

			String updateSubsdcriptionUrl = usersServiceUrl + ProveedorControllerUrls.GET_PROVEEDOR_SUSCRIPCION
					.replace("{proveedorId}", payment.getuserId().toString());

			SuscriptionActiveUpdateDTO requestBody = new SuscriptionActiveUpdateDTO(payment.getSuscripcion().getId(),
					newState.getState().equals(UalaPaymentStateValue.APPROVED.name()));

			httpClient.put(updateSubsdcriptionUrl, requestBody);
		});

	}

}
