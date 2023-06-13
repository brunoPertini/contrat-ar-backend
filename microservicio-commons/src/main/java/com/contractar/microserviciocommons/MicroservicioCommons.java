package com.contractar.microserviciocommons;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class MicroservicioCommons {

	public static void main(String[] args) {
		SpringApplication.run(MicroservicioCommons.class, args);
	}
	
}
