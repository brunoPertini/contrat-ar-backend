package com.contractar.microserviciovendible;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = { "com.contractar.microserviciousuario.models"})
public class MicroservicioVendibleApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroservicioVendibleApplication.class, args);
	}

}
