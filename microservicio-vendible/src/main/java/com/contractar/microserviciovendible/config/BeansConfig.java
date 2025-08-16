package com.contractar.microserviciovendible.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciocommons.infra.SecurityHelper;

@Configuration
public class BeansConfig {

	@Bean
	RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		return restTemplate;
	}

	@Bean
	SecurityHelper securityHelper() {
		return new SecurityHelper(restTemplate());
	}

}
