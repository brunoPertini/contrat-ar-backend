package com.contractar.microserviciopayment.providers;

public interface OutsitePaymentProvider<T, E> {
	public String auth();

	public String createCheckout();
}
