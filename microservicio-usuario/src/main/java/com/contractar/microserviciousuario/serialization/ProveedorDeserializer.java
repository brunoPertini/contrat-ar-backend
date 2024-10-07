package com.contractar.microserviciousuario.serialization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import com.contractar.microserviciocommons.constants.RolesNames.RolesValues;
import com.contractar.microserviciocommons.proveedores.ProveedorType;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.models.Role;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;

public class ProveedorDeserializer extends UserDeserializer {
	@Override
	public Proveedor deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {

		JsonNode node = jsonParser.getCodec().readTree(jsonParser);

		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

		setCommonUserInfo(node, grantedAuthorities);

		String dni = node.get("dni").asText();
		String fotoPerfilUrl = node.get("fotoPerfilUrl").asText();

		ProveedorType proveedorType = ProveedorType.valueOf(node.get("proveedorType").asText());
		Role chosenRole = proveedorType.equals(ProveedorType.PRODUCTOS)
				? new Role(RolesValues.PROVEEDOR_PRODUCTOS.toString())
				: new Role(RolesValues.PROVEEDOR_SERVICIOS.toString());

		return new Proveedor(name, surname, email, false, location, dni, password, null, null, birthDate,
				grantedAuthorities, chosenRole, proveedorType, fotoPerfilUrl, phone);

	}
}
