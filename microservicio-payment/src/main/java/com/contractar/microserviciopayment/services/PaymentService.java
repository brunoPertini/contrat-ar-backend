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
import com.contractar.microserviciocommons.constants.controllers.UsersControllerUrls;
import com.contractar.microserviciocommons.dto.SuscripcionDTO;
import com.contractar.microserviciocommons.dto.payment.PaymentInfoDTO;
import com.contractar.microserviciocommons.exceptions.payment.PaymentAlreadyDone;
import com.contractar.microserviciocommons.exceptions.payment.PaymentCantBeDone;
import com.contractar.microserviciocommons.exceptions.payment.PaymentNotFoundException;
import com.contractar.microserviciocommons.exceptions.proveedores.SuscriptionNotFound;
import com.contractar.microserviciocommons.mailing.PaymentLinkMailInfo;
import com.contractar.microserviciopayment.dtos.AuthResponse;
import com.contractar.microserviciopayment.dtos.PaymentCreateDTO;
import com.contractar.microserviciopayment.models.OutsitePaymentProviderImpl;
import com.contractar.microserviciopayment.models.Payment;
import com.contractar.microserviciopayment.models.PaymentProvider;
import com.contractar.microserviciopayment.models.PaymentState;
import com.contractar.microserviciopayment.models.SuscriptionPayment;
import com.contractar.microserviciopayment.models.enums.IntegrationType;
import com.contractar.microserviciopayment.models.enums.UalaPaymentStateValue;
import com.contractar.microserviciopayment.providers.PaymentUrls;
import com.contractar.microserviciopayment.providers.uala.WebhookBody;
import com.contractar.microserviciopayment.repository.OutsitePaymentProviderRepository;
import com.contractar.microserviciopayment.repository.PaymentProviderRepository;
import com.contractar.microserviciopayment.repository.PaymentRepository;
import com.contractar.microserviciopayment.repository.SuscriptionPaymentRepository;
import com.contractar.microserviciopayment.repository.UalaPaymentStateRepository;

import jakarta.transaction.Transactional;

@Service
public class PaymentService {

	private PaymentProviderRepository paymentProviderRepository;

	private OutsitePaymentProviderRepository outsitePaymentProviderRepository;

	private PaymentRepository paymentRepository;

	private SuscriptionPaymentRepository suscriptionPaymentRepository;

	private UalaPaymentStateRepository ualaPaymentStateRepository;

	private SuscriptionPaymentService suscriptionPaymentService;

	private RestTemplate httpClient;

	private ProviderServiceImplFactory providerServiceImplFactory;

	@Value("${microservicio-config.url}")
	private String configServiceUrl;

	@Value("${frontend.payment.signup.suscription}")
	private String frontendReturnUrl;

	@Value("${fontend.payment.userProfile.suscription}")
	private String userProfileReturnUrl;

	@Value("${provider.uala.webhookUrl}")
	private String webhookUrl;

	@Value("${microservicio-security.url}")
	private String serviceSecurityUrl;

	@Value("${microservicio-usuario.url}")
	private String microservicioUsuarioUrl;

	@Value("${provider.uala.webhookUrl.planChange}")
	private String webhookPlanChangeUrl;
	
	@Value("${microservicio-mailing.url}")
	private String microservicioMailingUrl;

	private static int PAYMENT_URL_MINUTES_DURATION = 15;

	public enum PAYMENT_SOURCES {
		SIGNUP, PROFILE,
	}

	public PaymentService(OutsitePaymentProviderRepository outsitePaymentProviderRepository,
			PaymentProviderRepository paymentProviderRepository, PaymentRepository paymentRepository,
			ProviderServiceImplFactory providerServiceImplFactory,
			SuscriptionPaymentRepository suscriptionPaymentRepository,
			UalaPaymentStateRepository ualaPaymentStateRepository, RestTemplate httpClient,
			SuscriptionPaymentService suscriptionPaymentService) {
		this.providerServiceImplFactory = providerServiceImplFactory;
		this.httpClient = httpClient;
		this.paymentProviderRepository = paymentProviderRepository;
		this.outsitePaymentProviderRepository = outsitePaymentProviderRepository;
		this.suscriptionPaymentRepository = suscriptionPaymentRepository;
		this.paymentRepository = paymentRepository;
		this.ualaPaymentStateRepository = ualaPaymentStateRepository;
		this.suscriptionPaymentService = suscriptionPaymentService;
	}

	private String getMessageTag(String tagId) {
		final String fullUrl = configServiceUrl + "/i18n/" + tagId;
		return httpClient.getForObject(fullUrl, String.class);
	}
	
	private Map<String, Object> getUserPayloadFromToken(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);

		UriComponentsBuilder tokenCheckUrlBuilder = UriComponentsBuilder.fromHttpUrl(serviceSecurityUrl)
				.path(SecurityControllerUrls.GET_USER_PAYLOAD_FROM_TOKEN);

		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<Map> response = httpClient.exchange(tokenCheckUrlBuilder.toUriString(), HttpMethod.GET, entity,
				Map.class);

		return response.getBody();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private boolean checkUserToken(String token) {
		Map<String, Object> payload = this.getUserPayloadFromToken(token);

		long expiresField = ((Number) payload.get("exp")).longValue();

		LocalDateTime dateTimeFromUnix = Instant.ofEpochSecond(expiresField).atZone(ZoneId.systemDefault())
				.toLocalDateTime();

		LocalDate today = LocalDate.now();

		LocalDate dateFromUnix = dateTimeFromUnix.toLocalDate();

		return !dateFromUnix.isBefore(today);

	}
	
	private void sendPaymentLinkEmail(String userToken, String paymentLink) {
		Map<String, Object> userInfo = this.getUserPayloadFromToken(userToken);
		
		PaymentLinkMailInfo mailInfo = new PaymentLinkMailInfo((String)userInfo.get("sub"),
				(String) userInfo.get("name"),
				paymentLink);
		
		String url = microservicioMailingUrl + UsersControllerUrls.PAYMENT_LINK_EMAIL;
		
		httpClient.postForEntity(url, mailInfo, Void.class);
	}

	private PaymentState fetchSuccessPaymentStateEntity() {
		// TODO: decouple harcoded provider
		return ualaPaymentStateRepository.findByState(UalaPaymentStateValue.APPROVED.name()).map(state -> state).orElse(null);
	}

	public PaymentInfoDTO getPaymentInfo(Long paymentId) {
		return this.paymentRepository.findById(paymentId)
				.map(p -> new PaymentInfoDTO(p.getId(), p.getExternalId(), p.getPaymentPeriod(), p.getDate(),
						p.getAmount(), p.getCurrency(), p.getState().toString(), p.getPaymentProvider().getName()))
				.orElse(null);
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

	private PaymentUrls resolvePaymentUrls(PAYMENT_SOURCES source, String createdPaymentId, String returnTab) {
		com.contractar.microserviciopayment.providers.OutsitePaymentProvider paymentProviderImpl = providerServiceImplFactory
				.getOutsitePaymentProvider();

		String basePath = source.equals(PAYMENT_SOURCES.SIGNUP) ? frontendReturnUrl : userProfileReturnUrl;

		String successReturnUrl = basePath.replace("{paymentStatus}", paymentProviderImpl.getSuccessStateValue())
				.replace("{paymentId}", createdPaymentId);

		String errorReturnUrl = basePath.replace("{paymentStatus}", paymentProviderImpl.getFailureStateValue())
				.replace("{paymentId}", createdPaymentId);

		String resolvedWebhookUrl = source.equals(PAYMENT_SOURCES.SIGNUP) ? webhookUrl : webhookPlanChangeUrl;

		if (StringUtils.hasLength(returnTab)) {
			successReturnUrl += "&returnTab=" + returnTab;
			errorReturnUrl += "&returnTab=" + returnTab;
		}

		return new PaymentUrls(successReturnUrl, errorReturnUrl, resolvedWebhookUrl);

	}

	public SuscriptionPayment findLastSuscriptionPayment(Long suscriptionId) {
		return suscriptionPaymentRepository.findTopBySuscripcionIdOrderByPaymentPeriodDesc(suscriptionId)
				.map(payment -> payment).orElse(null);
	}

	public SuscriptionPayment findLastSuccesfullSuscriptionPayment(Long suscriptionId) {
		PaymentState succesfullPaymentState = this.fetchSuccessPaymentStateEntity();
		return suscriptionPaymentRepository
				.findTopBySuscripcionIdAndStateOrderByPaymentPeriodDesc(suscriptionId, succesfullPaymentState)
				.map(payment -> payment).orElse(null);
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
	 * @param suscriptionId id of subscription to be payed
	 * @param source        Where from its payed in frontend
	 * @param returnTab     If the return urls should include this param to open
	 *                      some tab in frontemd
	   @param toBindUserId used for subscription payments when a user changes its plan
	   @param userToken logged user info
 	 * @return The checkout url to be used by the frontend so the user can finish
	 *         the pay there
	 * @throws SuscriptionNotFound
	 * @throws PaymentAlreadyDone
	 * @throws PaymentCantBeDone
	 * @throws PaymentNotFoundException
	 */
	@Transactional
	public String payLastSuscriptionPeriod(Long suscriptionId, PAYMENT_SOURCES source, String returnTab,
			Long toBindUserId, String userToken, Long promotionId) throws SuscriptionNotFound, PaymentCantBeDone {
		PaymentProvider currentProvider = this.getActivePaymentProvider();

		SuscripcionDTO foundSuscription = this.getSuscription(suscriptionId);

		// FREE plan cant be payed
		if (foundSuscription.getPlanId() == 1) {
			throw new PaymentCantBeDone(getMessageTag("exception.payment.cantBePayed"));
		}

		// TODO: receive by param the integration type. Depending on that, fetch the
		// proper payment provider configuration and use the required
		// provider services/entities, etc.
		OutsitePaymentProviderImpl activePaymentProvider = (OutsitePaymentProviderImpl) currentProvider;
		String authToken = activePaymentProvider.getToken();

		com.contractar.microserviciopayment.providers.OutsitePaymentProvider paymentProviderImpl = providerServiceImplFactory
				.getOutsitePaymentProvider();

		if (!suscriptionPaymentService.canSuscriptionBePayed(suscriptionId)) {
			return "";
		}

		SuscriptionPayment lastPayment = this.findLastSuscriptionPayment(suscriptionId);

		if (lastPayment != null && paymentProviderImpl.isPaymentPending(lastPayment)) {
			boolean isLinkValid = Optional.ofNullable(lastPayment.getLinkCreationTime()).isPresent() &&
					Duration.between(lastPayment.getLinkCreationTime(), LocalDateTime.now())
					.toMinutes() <= PAYMENT_URL_MINUTES_DURATION;

			if (isLinkValid) {
				return lastPayment.getPaymentUrl();
			}
		}

		YearMonth lastPeriodPayment = Optional.ofNullable(lastPayment).map(Payment::getPaymentPeriod).orElse(null);

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

		createdPayment.setuserId(toBindUserId);
		
		Optional.ofNullable(promotionId).ifPresent(createdPayment::setPromotionId);

		String createdPaymentId = createdPayment.getId().toString();

		PaymentUrls urls = this.resolvePaymentUrls(source, createdPaymentId, returnTab);

		String checkoutUrl = paymentProviderImpl.createCheckout(foundSuscription.getPlanPrice(),
				getMessageTag("payment.suscription.description"), createdPayment.getId(), urls, authToken);

		createdPayment.setPaymentUrl(checkoutUrl);

		createdPayment.setLinkCreationTime(LocalDateTime.now());
		
		this.sendPaymentLinkEmail(userToken, checkoutUrl);

		return checkoutUrl;

	}

	public void handleWebhookNotification(WebhookBody body) {
		com.contractar.microserviciopayment.providers.OutsitePaymentProvider paymentProviderImpl = providerServiceImplFactory
				.getOutsitePaymentProvider();
		paymentProviderImpl.handleWebhookNotification(body);
	}

	public void handleWebhookPlanChangeNotification(WebhookBody body) {
		com.contractar.microserviciopayment.providers.OutsitePaymentProvider paymentProviderImpl = providerServiceImplFactory
				.getOutsitePaymentProvider();
		paymentProviderImpl.handleWebhookPlanChangeNotification(body);
	}

	public boolean wasPaymentAccepted(Long paymentId) {
		com.contractar.microserviciopayment.providers.OutsitePaymentProvider paymentProviderImpl = providerServiceImplFactory
				.getOutsitePaymentProvider();
		return paymentRepository.findById(paymentId).map(p -> paymentProviderImpl.wasPaymentAccepted(p)).orElse(false);
	}

}
