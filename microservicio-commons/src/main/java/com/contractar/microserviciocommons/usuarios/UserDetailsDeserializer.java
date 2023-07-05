package com.contractar.microserviciocommons.usuarios;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.contractar.microserviciousuario.models.Usuario;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class UserDetailsDeserializer extends JsonDeserializer<Usuario> {

	@Override
	public Usuario deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JacksonException {
		JsonNode node = jsonParser.getCodec().readTree(jsonParser);
		
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		
		Usuario usuario = new Usuario();
		
		
		String name = node.get("name").asText();
		String surname = node.get("surname").asText();
		String password = node.get("password").asText();
		String email = node.get("email").asText();
	    LocalDate birthDate = LocalDate.parse(node.get("birthDate").asText());
        
	    Iterator<JsonNode> elements = node.get("authorities").elements();
	    
	    while (elements.hasNext()) {
	        JsonNode next = elements.next();
	        JsonNode authority = next.get("authority");
	        grantedAuthorities.add(new SimpleGrantedAuthority(authority.asText()));
	    }
	    
	    usuario.setname(name);
	    usuario.setsurname(surname);
	    usuario.setPassword(password);
	    usuario.setEmail(email);
	    usuario.setBirthDate(birthDate);
	    
		return usuario;
	}

}
