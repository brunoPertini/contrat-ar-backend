package com.contractar.microserviciousuario.serialization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;

import com.contractar.microserviciocommons.constants.RolesNames.RolesValues;
import com.contractar.microserviciousuario.models.Cliente;
import com.contractar.microserviciousuario.models.Role;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;

public class ClienteDeserializer extends UserDeserializer{
	
	@Override
	public Cliente deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JacksonException {
		JsonNode node = jsonParser.getCodec().readTree(jsonParser);

		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		
		setCommonUserInfo(node, grantedAuthorities);

		return new Cliente(null, name, surname, email, false, location, birthDate, password, grantedAuthorities, new Role(RolesValues.CLIENTE.toString()));
	}

}
