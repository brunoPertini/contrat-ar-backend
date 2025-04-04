package com.contractar.microserviciopayment.providers;

import com.contractar.microserviciopayment.models.Payment;

/**
 * An outsite payment provider creates an url where the payment is performed.
 * Usually, it is told where to tell about payment operations states (for
 * instance, a webhook)
 * 
 * @param <E> CheckoutBody class
 * @param <M> Entity class linked to this provider
 * @param <R> Authorization service respone class
 * @param <D> Webhook body type
 */
public interface OutsitePaymentProvider<E, M, R, D> {
	public R auth();

	public String createCheckout(int amount, String description, Long externalReference, PaymentUrls urls,
			String authToken);

	public E createCheckoutBody(int amount, String description, String callbackFail, String callbackSuccess,
			String notificationUrl, Long externalReference);

	public M save(M entity);

	public void setPaymentAsPending(Payment p);

	public void handleWebhookNotification(D body);
	
	public void handleWebhookPlanChangeNotification(D body);

	public boolean wasPaymentRejected(Payment payment);

	public boolean wasPaymentAccepted(Payment payment);
	
	public boolean isPaymentPending(Payment payment);
	
	public boolean isPaymentProcessed(Payment payment);
	
	public String getSuccessStateValue();
	
	public String getFailureStateValue();

}
