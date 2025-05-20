package com.contractar.microserviciousuario.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.contractar.microserviciocommons.filters.InternalTokenFilter;

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
