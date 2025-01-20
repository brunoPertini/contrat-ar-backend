package com.contractar.microserviciopayment.providers;

/**
 * Holds urls to handle- outsite providers functionality, as follows:
 * - successUrl: tells provider where to redirect if payment is successful
 * - failUrl: same as previous but to manage failure cases.
 * - notificationUrl: here we should expose a public endpoint URL, capable of receiving payment info after
 * 	 having been handled by the provider
 */
public class PaymentUrls {
	private String successUrl;
	private String failUrl;
	private String notificationUrl;

	public PaymentUrls() {
	}

	public PaymentUrls(String successUrl, String failUrl, String notificationsUrl) {
		this.successUrl = successUrl;
		this.failUrl = failUrl;
		this.notificationUrl = notificationsUrl;
	}

	public String getSuccessUrl() {
		return successUrl;
	}

	public void setSuccessUrl(String successUrl) {
		this.successUrl = successUrl;
	}

	public String getFailUrl() {
		return failUrl;
	}

	public void setFailUrl(String failUrl) {
		this.failUrl = failUrl;
	}

	public String getNotificationUrl() {
		return notificationUrl;
	}

	public void setNotificationUrl(String notificationsUrl) {
		this.notificationUrl = notificationsUrl;
	}
}
