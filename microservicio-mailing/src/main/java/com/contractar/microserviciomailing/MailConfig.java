package com.contractar.microserviciomailing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MailConfig {
    
    @Bean
    RestTemplate httpClient() {
    	return new RestTemplate();
    }
}
