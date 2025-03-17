package com.contractar.microserviciocommons.constants.controllers;

public final class PaymentControllerUrls {
	private PaymentControllerUrls() {}

	public static final String PAYMENT_BASE_URL = "/payment";
	
	public static final String SUSCRIPTION_BY_ID = "/suscription/{suscriptionId}";
	
	public static final String IS_SUSCRIPTION_PAYABLE = SUSCRIPTION_BY_ID + "/payable";
	
	public static final String SUSCRIPTION_PAYMENT_BASE_URL = PAYMENT_BASE_URL + SUSCRIPTION_BY_ID;
	
	public static final String LAST_SUSCRIPTION_PAYMENT_BASE_URL = SUSCRIPTION_BY_ID + PAYMENT_BASE_URL;
	
	public static final String PAYMENT_BY_ID = PAYMENT_BASE_URL + "/{id}";
	
	public static final String PAYMENT_SIGNUP_SUSCRIPTION = PAYMENT_BASE_URL + "/signup/{suscriptionId}";
	
	public static final String PAYMENT_WEBHOOK_URL = PAYMENT_BASE_URL + "/notification";
	
	public static final String PAYMENT_PROVIDER = PAYMENT_BASE_URL + "/provider";
}
