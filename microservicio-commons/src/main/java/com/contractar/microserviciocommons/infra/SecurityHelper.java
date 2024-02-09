package com.contractar.microserviciocommons.infra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciooauth.helpers.JwtHelper;
import com.fasterxml.jackson.core.JsonProcessingException;

@Component
public class SecurityHelper {
	
	private RestTemplate httpClient;
	
	private JwtHelper jwtHelper;

    @Autowired
    public SecurityHelper(RestTemplate restTemplate, JwtHelper jwtHelper) {
        this.httpClient = restTemplate;
        this.jwtHelper = jwtHelper;
    }
	
	public boolean isResponseContentTypeValid(String url, String expectedContentType) {
		try {
			ResponseEntity<Void> response = httpClient.getForEntity(url, Void.class);
			MediaType responseMediaType = response.getHeaders().getContentType();
			boolean isResponseOK = response.getStatusCode().is2xxSuccessful();
			return isResponseOK 
					&& responseMediaType != null
					&& responseMediaType.toString().contains(expectedContentType);
		} catch (Exception e) {
			return false;
		}
	}
	
	public Object getValueFromJwt(String valueKey, String jwt) throws JsonProcessingException {
		return jwtHelper.parsePayloadFromJwt(jwt).get(valueKey);
	}

}
