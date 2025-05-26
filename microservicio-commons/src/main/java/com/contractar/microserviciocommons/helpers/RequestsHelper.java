package com.contractar.microserviciocommons.helpers;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class RequestsHelper {
	@Value("${USER}")
	private String configServiceAuthUsername;

	@Value("${PASS}")
	private String configServiceAuthPassword;

	public  void setBasicAuthHeader(HttpHeaders headers) {
		String credentials = configServiceAuthUsername + ":" + configServiceAuthPassword;
		String base64Creds = Base64.getEncoder().encodeToString(credentials.getBytes());

		headers.add("Authorization", "Basic " + base64Creds);
	}
}
