package com.contractar.microserviciousuario.serialization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciocommons.constants.RolesNames.RolesValues;
import com.contractar.microserviciocommons.constants.controllers.ProveedorControllerUrls;
import com.contractar.microserviciocommons.exceptions.vendibles.CantCreateException;
import com.contractar.microserviciocommons.proveedores.ProveedorType;
import com.contractar.microserviciousuario.models.Plan;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.models.Role;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ProveedorDeserializer extends UserDeserializer {
	@Override
	public Proveedor deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
		RestTemplate restTemplate = (RestTemplate) ctxt.findInjectableValue(RestTemplate.class.getName(), null, null);
		String microservicioUsuarioUrl = (String) ctxt.findInjectableValue("microservicioUsuarioUrl", null, null);

		JsonNode node = jsonParser.getCodec().readTree(jsonParser);

		String planId = node.get("plan").asText();
		String getPlanUrl = microservicioUsuarioUrl
				+ ProveedorControllerUrls.GET_PLAN_BY_ID.replace("{planId}", planId);

		// ResponseEntity<Plan> getPlanResponse = restTemplate.getForEntity(getPlanUrl, Plan.class);

		/*
		 * if (getPlanResponse.getStatusCode() != HttpStatusCode.valueOf(200)) { throw
		 * new IOException(CantCreateException.message); }
		 */

		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

		setCommonUserInfo(node, grantedAuthorities);

		String dni = node.get("dni").asText();
		String fotoPerfilUrl = node.get("fotoPerfilUrl").asText();

		ProveedorType proveedorType = ProveedorType.valueOf(node.get("proveedorType").asText());
		// Plan plan = getPlanResponse.getBody();
		Role chosenRole = proveedorType.equals(ProveedorType.PRODUCTOS)
				? new Role(RolesValues.PROVEEDOR_PRODUCTOS.toString())
				: new Role(RolesValues.PROVEEDOR_SERVICIOS.toString());

		return new Proveedor(name, surname, email, false, location, dni, password, null, null, birthDate,
				grantedAuthorities, chosenRole, proveedorType, fotoPerfilUrl, phone);

	}
}
