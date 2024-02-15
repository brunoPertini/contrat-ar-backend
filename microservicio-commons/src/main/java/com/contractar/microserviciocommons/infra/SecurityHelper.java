package com.contractar.microserviciocommons.infra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SecurityHelper {

	private RestTemplate httpClient;

	@Autowired
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
