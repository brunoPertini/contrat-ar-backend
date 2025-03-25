package com.contractar.microserviciogateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciogateway.filters.PlanCancelUrlChangeFilter;

@Configuration
public class BeansConfig {

    @Bean
    RestTemplate restTemplate() {
		return new RestTemplate();
	}
    
    @Bean
    PlanCancelUrlChangeFilter planChangeCancelFilter() {
        return new PlanCancelUrlChangeFilter();
    }

}