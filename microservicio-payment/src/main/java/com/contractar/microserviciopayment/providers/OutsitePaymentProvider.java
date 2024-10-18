package com.contractar.microserviciopayment.providers;

import com.contractar.microserviciopayment.services.PaymentService.PaymentUrls;

public interface OutsitePaymentProvider<T, E, M> {
	public String auth();

	public String createCheckout(int amount, String description, PaymentUrls urls, String authToken);
	
	public E createCheckoutBody(int amount, String description, String callbackFail, String callbackSuccess,
			String notificationUrl);
	
	public  M save(M entity);
}
