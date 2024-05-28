package com.contractar.microserviciousuario.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciocommons.infra.SecurityHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Configuration
public class BeansConfig {
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
    @Bean
    public SecurityHelper securityHelper() {
    	return  new SecurityHelper(restTemplate());
    }
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
    	return new BCryptPasswordEncoder();
    }
}
