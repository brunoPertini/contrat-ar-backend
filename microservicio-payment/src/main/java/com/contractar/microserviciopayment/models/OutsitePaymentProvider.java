package com.contractar.microserviciopayment.models;

public interface OutsitePaymentProvider extends PaymentProviderAccesor {
	public String getToken();
	
	public void setToken(String token);
	
}
