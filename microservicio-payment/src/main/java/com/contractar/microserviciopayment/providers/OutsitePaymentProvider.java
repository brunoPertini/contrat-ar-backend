package com.contractar.microserviciopayment.providers;

import com.contractar.microserviciopayment.models.Payment;
import com.contractar.microserviciopayment.services.PaymentService.PaymentUrls;

/**
 * An outsite payment provider creates an url where the payment is performed. Usually, it is told
 * where to tell about payment operations states (for instance, a webhook)
 * @param <E> CheckoutBody class
 * @param <M> Entity class linked to this provider
 * @param <R> Authorization service respone class
 */
public interface OutsitePaymentProvider<E, M, R> {
	public R auth();

	public String createCheckout(int amount, String description, PaymentUrls urls, String authToken);
	
	public E createCheckoutBody(int amount, String description, String callbackFail, String callbackSuccess,
			String notificationUrl);
	
	public  M save(M entity);
	
	public void setPaymentAsPending(Payment p);
}
