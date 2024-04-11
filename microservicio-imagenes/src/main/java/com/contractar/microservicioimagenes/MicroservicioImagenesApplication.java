package com.contractar.microservicioimagenes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class} )
public class MicroservicioImagenesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroservicioImagenesApplication.class, args);
	}

}
