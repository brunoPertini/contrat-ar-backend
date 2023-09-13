package com.contractar.microserviciocommons.usuarios.serialization;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

@SuppressWarnings("rawtypes")
public abstract class UserDeserializer extends JsonDeserializer {
	protected String name;
	protected String surname;
	protected String password;
	protected String email;
	protected LocalDate birthDate;
	protected Point location;

	protected void setCommonUserInfo(JsonNode node, List<GrantedAuthority> grantedAuthorities) throws IOException {
		name = node.get("name").asText();
		surname = node.get("surname").asText();
		password = node.get("password").asText();
		email = node.get("email").asText();
		birthDate = LocalDate.parse(node.get("birthDate").asText());

		if (Optional.ofNullable(node.get("authorities")).isPresent()) {
			Iterator<JsonNode> elements = node.get("authorities").elements();

			while (elements.hasNext()) {
				JsonNode next = elements.next();
				JsonNode authority = next.get("authority");
				grantedAuthorities.add(new SimpleGrantedAuthority(authority.asText()));
			}
		}

		double x = node.get("location").get("x").asDouble();
		double y = node.get("location").get("y").asDouble();
		location = new GeometryFactory().createPoint(new Coordinate(x, y));

	}
}
