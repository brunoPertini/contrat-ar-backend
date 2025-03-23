package com.contractar.microserviciopayment.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.contractar.microserviciocommons.constants.controllers.PaymentControllerUrls;
import com.contractar.microserviciocommons.dto.payment.PaymentInfoDTO;
import com.contractar.microserviciocommons.exceptions.payment.PaymentAlreadyDone;
import com.contractar.microserviciocommons.exceptions.payment.PaymentCantBeDone;
import com.contractar.microserviciocommons.exceptions.proveedores.SuscriptionNotFound;
import com.contractar.microserviciopayment.dtos.PaymentCreateDTO;
import com.contractar.microserviciopayment.dtos.PaymentDTO;
import com.contractar.microserviciopayment.dtos.PaymentProviderDTO;
import com.contractar.microserviciopayment.models.Payment;
import com.contractar.microserviciopayment.providers.uala.WebhookBody;
import com.contractar.microserviciopayment.services.PaymentService;
import com.contractar.microserviciopayment.services.PaymentService.PAYMENT_SOURCES;
import com.contractar.microserviciopayment.services.ProviderServiceImplFactory;
import com.contractar.microserviciopayment.services.SuscriptionPaymentService;

import jakarta.validation.Valid;

@RestController
public class PaymentController {
	
	private PaymentService paymentService;
	private SuscriptionPaymentService suscriptionPaymentService;
	
	public PaymentController(PaymentService paymentService, ProviderServiceImplFactory providerServiceImplFactory, SuscriptionPaymentService suscriptionPaymentService) {
		this.paymentService = paymentService;
		this.suscriptionPaymentService = suscriptionPaymentService;
	}
	
	@PostMapping(PaymentControllerUrls.PAYMENT_SIGNUP_SUSCRIPTION)
	public ResponseEntity<String> paySignupSuscription(@RequestBody @Valid PaymentDTO body, @PathVariable Long suscriptionId) throws SuscriptionNotFound,
	PaymentAlreadyDone, PaymentCantBeDone {
		String checkoutUrl = paymentService.payLastSuscriptionPeriod(suscriptionId, PAYMENT_SOURCES.SIGNUP, null);
		return ResponseEntity.ok(checkoutUrl);
	}
	
	@PostMapping(PaymentControllerUrls.PAYMENT_USER_PROFILE_SUSCRIPTION)
	public ResponseEntity<String> payUserProfileSubscription(@RequestBody @Valid PaymentDTO body, 
			@PathVariable Long suscriptionId,
			@RequestParam(required = true) String returnTab) throws SuscriptionNotFound, 
	PaymentCantBeDone {
		String checkoutUrl = paymentService.payLastSuscriptionPeriod(suscriptionId, PAYMENT_SOURCES.PROFILE, returnTab);
		return ResponseEntity.ok(checkoutUrl);
	}
	
	@GetMapping(PaymentControllerUrls.PAYMENT_BY_ID)
	public ResponseEntity<?> getPaymentInfo(@PathVariable Long id) {
		PaymentInfoDTO dto = paymentService.getPaymentInfo(id);
		
		return dto != null ? new ResponseEntity(dto, HttpStatus.OK) : new ResponseEntity(HttpStatusCode.valueOf(404));
 	}
	
	@GetMapping(PaymentControllerUrls.IS_SUSCRIPTION_VALID)
	public ResponseEntity<Boolean> isSuscriptionValid(@PathVariable Long suscriptionId) {
		return new ResponseEntity<>(suscriptionPaymentService.isSuscriptionValid(suscriptionId),  HttpStatus.OK);
	}
	
	@GetMapping(PaymentControllerUrls.SUSCRIPTION_PAYMENT_BASE_URL)
	public ResponseEntity<List<PaymentInfoDTO>> getSubscriptionPayments(@PathVariable Long suscriptionId) {
		return new ResponseEntity<>(suscriptionPaymentService.getPaymentsOfSubscription(suscriptionId),  HttpStatus.OK);
	}
	
	@GetMapping(PaymentControllerUrls.LAST_SUSCRIPTION_PAYMENT_BASE_URL)
	public ResponseEntity<PaymentInfoDTO> getLastSuscriptionPayment(@PathVariable Long suscriptionId) {
		Payment payment = paymentService.findLastSuccesfullSuscriptionPayment(suscriptionId);
		PaymentInfoDTO response = payment != null ? new PaymentInfoDTO(payment.getId(), payment.getExternalId(),
				payment.getPaymentPeriod(), payment.getDate(), payment.getAmount(), payment.getCurrency(), payment.getState().toString(),
				payment.getPaymentProvider().getName()) : new PaymentInfoDTO();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping(PaymentControllerUrls.PAYMENT_BASE_URL)
	public ResponseEntity<?> createPayment(@RequestBody @Valid PaymentCreateDTO body) {
		return new ResponseEntity<>(paymentService.createPayment(body), HttpStatusCode.valueOf(200));
	}
	
	@GetMapping(PaymentControllerUrls.IS_SUSCRIPTION_PAYABLE)
	public ResponseEntity<Boolean> isSubscriptionPayable(@PathVariable Long suscriptionId) {
		return new ResponseEntity<>(suscriptionPaymentService.canSuscriptionBePayed(suscriptionId), HttpStatus.OK);
	}
	
	@PostMapping(PaymentControllerUrls.PAYMENT_WEBHOOK_URL)
	public ResponseEntity<?> postPaymentUpdate(@RequestBody WebhookBody body) {
		paymentService.handleWebhookNotification(body);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@GetMapping(PaymentControllerUrls.PAYMENT_PROVIDER)
	public ResponseEntity<?> getActiveProviderInfo() {
		return new ResponseEntity<>(new PaymentProviderDTO(paymentService.getActivePaymentProvider()) , HttpStatus.OK);
	}

}
