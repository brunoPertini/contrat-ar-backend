package com.contractar.microserviciocommons.helpers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;

@Service
public final class DatesHelper {
    private RestTemplate httpClient;
    
    private RequestsHelper requestsHelper;
    
    @Value("${microservicio-config.url}")
    private String serviceConfigUrl;
    
    private String getMessageUrl;
    
    @PostConstruct
    public void init() {
    	this.getMessageUrl = serviceConfigUrl + "/i18n/";
    }
    	
	public DatesHelper(RestTemplate restTemplate, RequestsHelper requestsHelper) {
		this.httpClient = restTemplate;
		this.requestsHelper = requestsHelper;
	}
	
	private HttpEntity<Void> createRequestWithAuthHeaders() {
	    HttpHeaders headers = new HttpHeaders();
	    requestsHelper.setBasicAuthHeader(headers);
	    return new HttpEntity<>(headers);
	}
	
	public String getMonthAndYearPattern() {		
		 return httpClient.exchange(
				 getMessageUrl+"date.format.month.and.year",
	                HttpMethod.GET,
	                createRequestWithAuthHeaders(),
	                String.class
	        ).getBody();
	}
	
	public String getFullDatePattern() {		
		 return httpClient.exchange(
				 getMessageUrl+"date.format.default",
	                HttpMethod.GET,
	                createRequestWithAuthHeaders(),
	                String.class
	        ).getBody();
	}
}
