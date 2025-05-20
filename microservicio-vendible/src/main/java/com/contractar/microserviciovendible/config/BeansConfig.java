package com.contractar.microserviciovendible.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciocommons.filters.InternalTokenFilter;
import com.contractar.microserviciocommons.infra.SecurityHelper;

import jakarta.servlet.Filter;

@Configuration
public class BeansConfig {
	@Value("${INTERNAL_KEY}")
	private String token;

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
    
    @Bean
    Filter internalAccessFilter() {
        return new InternalTokenFilter(token);
    }
}
