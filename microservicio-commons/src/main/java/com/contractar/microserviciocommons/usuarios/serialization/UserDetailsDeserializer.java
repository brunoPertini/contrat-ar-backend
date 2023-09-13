package com.contractar.microserviciocommons.usuarios.serialization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;

import com.contractar.microserviciousuario.models.Role;
import com.contractar.microserviciousuario.models.Usuario;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;

public class UserDetailsDeserializer extends UserDeserializer {

	@Override
	public Usuario deserialize(JsonParser jsonParser, DeserializationContext ctxt)
			throws IOException, JacksonException {
		JsonNode node = jsonParser.getCodec().readTree(jsonParser);

		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

		setCommonUserInfo(node, grantedAuthorities);

		Role role = Optional.ofNullable(node.get("role")).isPresent()
				? new Role((node.get("role").get("nombre").asText()))
				: null;

		Usuario usuario = new Usuario(null, name, surname, email, true, location, birthDate, password,
				grantedAuthorities, role);

		return usuario;
	}

}
