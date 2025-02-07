package com.contractar.microserviciooauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication()
@EnableJpaRepositories(basePackages = "com.contractar.microserviciooauth.repositories")
public class MicroservicioOauthApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroservicioOauthApplication.class, args);
	}

}
