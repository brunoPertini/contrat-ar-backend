package com.contractar.microserviciopayment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication()
@EntityScan(basePackages = { "com.contractar.microserviciousuario.models", "com.contractar.microserviciopayment.models"})
public class MicroservicioPaymentApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroservicioPaymentApplication.class, args);
	}

}
