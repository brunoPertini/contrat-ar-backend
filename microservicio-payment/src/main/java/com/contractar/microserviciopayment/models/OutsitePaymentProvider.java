package com.contractar.microserviciopayment.models;

public interface OutsitePaymentProvider {
	public String getToken();
	
	public void setToken(String token);
}
