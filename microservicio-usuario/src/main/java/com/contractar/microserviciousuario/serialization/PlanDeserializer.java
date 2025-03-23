package com.contractar.microserviciousuario.serialization;

import java.io.IOException;

import com.contractar.microservicioadapter.enums.PlanType;
import com.contractar.microserviciousuario.models.Plan;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PlanDeserializer extends JsonDeserializer<Plan> {

	@Override
	public Plan deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
		ObjectNode node = p.getCodec().readTree(p);
		
		Long id = node.get("id").asLong();
		
		String descripcion = node.get("descripcion").toString();
		
		PlanType planType = PlanType.valueOf(node.get("type").asText());
		
		int price = node.get("price").asInt();
		
		return new Plan(id, descripcion, planType, price);
	}

}
