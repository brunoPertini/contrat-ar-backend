package com.contractar.microserviciovendible;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"com.contractar.microserviciousuario.models", "com.contractar.microserviciovendible.models"})
public class MicroservicioVendibleApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroservicioVendibleApplication.class, args);
	}

}
