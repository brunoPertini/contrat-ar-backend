package com.contractar.microserviciocommons.helpers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;

@Service
public final class DatesHelper {
    private RestTemplate httpClient;
    
    @Value("${microservicio-config.url}")
    private String serviceConfigUrl;
    
    private String getMessageUrl;
    
    @PostConstruct
    public void init() {
    	this.getMessageUrl = serviceConfigUrl + "/i18n/";
    }
    	
	public DatesHelper(RestTemplate restTemplate) {
		this.httpClient = restTemplate;
	}
	
	public String getMonthAndYearPattern() {
		return  httpClient.getForObject(getMessageUrl+"date.format.month.and.year", String.class);
	}
	
	public String getFullDatePattern() {
		return  httpClient.getForObject(getMessageUrl+"date.format.default", String.class);
	}
}
