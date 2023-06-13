package com.contractar.microserviciooauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class MicroservicioOauthApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroservicioOauthApplication.class, args);
	}

}
