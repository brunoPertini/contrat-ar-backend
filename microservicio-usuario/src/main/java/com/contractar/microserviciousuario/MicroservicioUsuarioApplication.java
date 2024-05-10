package com.contractar.microserviciousuario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class })
@EntityScan(basePackages = { "com.contractar.microserviciousuario.models", "com.contractar.microserviciousuario.admin.models"})
public class MicroservicioUsuarioApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroservicioUsuarioApplication.class, args);
	}

}
