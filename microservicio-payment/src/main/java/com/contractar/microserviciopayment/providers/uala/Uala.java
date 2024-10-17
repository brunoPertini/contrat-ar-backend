package com.contractar.microserviciopayment.providers.uala;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciopayment.providers.OutsitePaymentProvider;

@Component
public class Uala implements OutsitePaymentProvider<AuthBody, CheckoutBody> {
	private static final String keysPrefix = "provider.uala";

	@Value("${" + keysPrefix + ".username}")
	private String username;

	@Value("${" + keysPrefix + ".clientId}")
	private String clientId;

	@Value("${" + keysPrefix + ".clientSecretId}")
	private String clientSecretId;

	@Value("${" + keysPrefix + ".authUrl}")
	private String authUrl;

	@Value("${" + keysPrefix + ".checkoutUrl}")
	private String checkoutUrl;
	
	private RestTemplate httpClient;
	
	public Uala(RestTemplate httpClient) {
		this.httpClient = httpClient;
	}

	@Override
	public String auth() {
		AuthBody authBody = new AuthBody(username, clientId, clientSecretId);
		
		return httpClient.postForObject(authUrl, authBody, String.class);
	}

	@Override
	public String createCheckout() {
		// TODO Auto-generated method stub
		return null;
	}

}
