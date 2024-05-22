package com.contractar.microserviciousuario.serialization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciocommons.constants.controllers.ProveedorControllerUrls;
import com.contractar.microserviciocommons.exceptions.vendibles.CantCreateException;
import com.contractar.microserviciocommons.proveedores.ProveedorType;
import com.contractar.microserviciousuario.models.Plan;
import com.contractar.microserviciousuario.models.Proveedor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;

public class ProveedorDeserializer extends UserDeserializer {
	private Proveedor proveedor;
	
	@Value("${microservicio-usuario.url}")
	private String microservicioUsuarioUrl;
	
	private RestTemplate restTemplate;
	
	public ProveedorDeserializer(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Override
	public Proveedor deserialize(JsonParser jsonParser, DeserializationContext ctxt)
			throws IOException {
		JsonNode node = jsonParser.getCodec().readTree(jsonParser);

		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		
		setCommonUserInfo(node, grantedAuthorities);
		
		String dni = node.get("dni").asText();
		String planId = node.get("plan").asText();
		String proveedorType = node.get("proveedorType").asText();
		
		String getPlanUrl = microservicioUsuarioUrl + ProveedorControllerUrls.GET_PLAN_BY_ID.replace("{planId}", planId);
		
		ResponseEntity<Plan> getPlanResponse = restTemplate.getForEntity(getPlanUrl, Plan.class);
		
		if (getPlanResponse.getStatusCode() != HttpStatusCode.valueOf(200)) {
			throw new IOException(CantCreateException.message);
		}
		
				
		Optional.ofNullable(node.get("proveedorType")).ifPresent(roleNode -> {	
				proveedor = new Proveedor(name, surname, email, false, location, dni, password, getPlanResponse.getBody(),
						null, birthDate, grantedAuthorities, null, ProveedorType.valueOf(proveedorType));

		});
		
		return proveedor;

	}
}
