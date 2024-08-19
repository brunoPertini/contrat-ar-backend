package com.contractar.microserviciousuario;

import org.locationtech.jts.geom.Point;
import org.n52.jackson.datatype.jts.JtsModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciocommons.infra.SecurityHelper;
import com.contractar.microserviciocommons.usuarios.UbicacionDeserializer;
import com.contractar.microserviciocommons.usuarios.UbicacionSerializer;
import com.contractar.microserviciousuario.models.Usuario;
import com.contractar.microserviciousuario.serialization.UserDetailsDeserializer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class MicroservicioUsuarioConfiguration {

	@Value("${microservicio-usuario.url}")
	private String microservicioUsuarioUrl;

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public SecurityHelper securityHelper() {
		return new SecurityHelper(restTemplate());
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.registerModule(new JtsModule());
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

		InjectableValues.Std injectableValues = new InjectableValues.Std();
		injectableValues.addValue(RestTemplate.class, new RestTemplate());
		injectableValues.addValue("microservicioUsuarioUrl", microservicioUsuarioUrl);

		objectMapper.setInjectableValues(injectableValues);

		SimpleModule module = new SimpleModule();
		module.addSerializer(Point.class, new UbicacionSerializer());
		module.addDeserializer(Point.class, new UbicacionDeserializer());
		module.addDeserializer(Usuario.class, new UserDetailsDeserializer());
		
		objectMapper.registerModule(module);

		return objectMapper;

	}
}
