package com.contractar.microserviciocommons.usuarios.serialization;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.locationtech.jts.geom.Point;
import org.springframework.security.core.GrantedAuthority;
import com.contractar.microserviciocommons.plans.PlanType;
import com.contractar.microserviciocommons.proveedores.ProveedorType;
import com.contractar.microserviciousuario.models.Proveedor;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class ProveedorDeserializer extends JsonDeserializer<Proveedor> {
	private Proveedor proveedor;

	@Override
	public Proveedor deserialize(JsonParser jsonParser, DeserializationContext ctxt)
			throws IOException, JacksonException {
		JsonNode node = jsonParser.getCodec().readTree(jsonParser);

		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

		String name = null;
		String surname = null;
		String password = null;
		String email = null;
		LocalDate birthDate = null;
		
		Point location = null;
		
		SerializationHelper.setCommonUserInfo(node, name, surname, password, email, birthDate, grantedAuthorities, null);

		String dni = node.get("dni").asText();
		String plan = node.get("plan").asText();
		String proveedorType = node.get("proveedorType").asText();
		
				
		Optional.ofNullable(node.get("proveedorType")).ifPresent(roleNode -> {	
				proveedor = new Proveedor(name, surname, email, false, location, dni, password, PlanType.valueOf(plan),
						null, birthDate, grantedAuthorities, null, ProveedorType.valueOf(proveedorType));

		});
		
		return proveedor;

	}
}
