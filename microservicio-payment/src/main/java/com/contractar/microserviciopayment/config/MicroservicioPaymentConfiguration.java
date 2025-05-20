package com.contractar.microserviciopayment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciocommons.filters.InternalTokenFilter;

import jakarta.servlet.Filter;

@Configuration
public class MicroservicioPaymentConfiguration {

	@Value("${INTERNAL_KEY}")
	private String token;

	@Bean
	RestTemplate httpClient() {
		return new RestTemplate();
	}

	@Bean
	Filter internalAccessFilter() {
		return new InternalTokenFilter(token);
	}

}
