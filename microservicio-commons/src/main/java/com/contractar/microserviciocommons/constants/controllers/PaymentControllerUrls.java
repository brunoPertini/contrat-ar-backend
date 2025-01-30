package com.contractar.microserviciocommons.constants.controllers;

public final class PaymentControllerUrls {
	public static final String PAYMENT_BASE_URL = "/payment";
	
	public static final String PAYMENT_BY_ID = PAYMENT_BASE_URL + "/{id}";
	
	public static final String PAYMENT_SIGNUP_SUSCRIPTION = PAYMENT_BASE_URL + "/signup/{suscriptionId}";
	
	public static final String PAYMENT_WEBHOOK_URL = PAYMENT_BASE_URL + "/notification";
	
	public static final String PAYMENT_PROVIDER = PAYMENT_BASE_URL + "/provider";
}
