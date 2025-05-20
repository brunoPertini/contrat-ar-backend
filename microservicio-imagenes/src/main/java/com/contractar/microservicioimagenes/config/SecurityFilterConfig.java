package com.contractar.microservicioimagenes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.contractar.microserviciocommons.filters.InternalTokenFilter;
import org.springframework.beans.factory.annotation.Value;
import jakarta.servlet.Filter;

@Configuration
public class SecurityFilterConfig {
	
	@Value("${INTERNAL_KEY}")
	private String token;

    @Bean
    Filter internalAccessFilter() {
        return new InternalTokenFilter(token);
    }
}
