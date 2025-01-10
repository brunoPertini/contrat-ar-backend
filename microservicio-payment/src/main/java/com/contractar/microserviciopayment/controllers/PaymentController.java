package com.contractar.microserviciopayment.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.contractar.microserviciocommons.exceptions.proveedores.SuscriptionNotFound;
import com.contractar.microserviciopayment.dtos.PaymentCreateDTO;
import com.contractar.microserviciopayment.dtos.PaymentDTO;
import com.contractar.microserviciopayment.providers.uala.WebhookBody;
import com.contractar.microserviciopayment.services.PaymentService;

import jakarta.validation.Valid;

@RestController
public class PaymentController {
	
	private PaymentService paymentService;

	public PaymentController(PaymentService paymentService) {
		this.paymentService = paymentService;
	}
	
	@PostMapping("/pay/signup/{suscriptionId}")
	public ResponseEntity<?> paySignupSuscription(@RequestBody @Valid PaymentDTO body, @PathVariable Long suscriptionId) throws SuscriptionNotFound {
		String checkoutUrl = paymentService.payLastSuscriptionPeriod(suscriptionId);
		return ResponseEntity.ok(checkoutUrl);
	}
	
	@PostMapping("/payment")
	public ResponseEntity<?> createPayment(@RequestBody @Valid PaymentCreateDTO body) {
		return new ResponseEntity<>(paymentService.createPayment(body), HttpStatusCode.valueOf(200));
	}
	
	@PostMapping("/payment/uala/notification")
	public ResponseEntity<?> postPaymentUpdate(@RequestBody WebhookBody body) {
		paymentService.handleWebhookNotification(body);
		return new ResponseEntity(HttpStatus.OK);
	}

}
