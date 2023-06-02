package com.contractar.microserviciogateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

import com.contractar.microserviciogateway.security.JwtConfig;

@SpringBootApplication
@EnableZuulProxy
@EnableResourceServer
public class MicroservicioGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroservicioGatewayApplication.class, args);
	}
	
}
