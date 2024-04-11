package com.contractar.microserviciocommons.infra;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class SecurityHelper {

	private RestTemplate httpClient;

	public SecurityHelper(RestTemplate restTemplate) {
		this.httpClient = restTemplate;
	}

	public boolean isResponseContentTypeValid(String url, String expectedContentType) {
		try {
			ResponseEntity<Void> response = httpClient.getForEntity(url, Void.class);
			MediaType responseMediaType = response.getHeaders().getContentType();
			boolean isResponseOK = response.getStatusCode().is2xxSuccessful();
			return isResponseOK && responseMediaType != null
					&& responseMediaType.toString().contains(expectedContentType);
		} catch (Exception e) {
			return false;
		}
	}
}
