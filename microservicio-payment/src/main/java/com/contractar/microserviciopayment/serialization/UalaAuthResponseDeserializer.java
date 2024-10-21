package com.contractar.microserviciopayment.serialization;

import java.io.IOException;

import com.contractar.microserviciopayment.providers.uala.UalaAuthResponse;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class UalaAuthResponseDeserializer extends JsonDeserializer<UalaAuthResponse> {

	@Override
	public UalaAuthResponse deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JacksonException {
		JsonNode node = p.getCodec().readTree(p);
		
		return new UalaAuthResponse(node.get("access_token").asText(),
									node.get("expires_in").asInt(),
									node.get("token_type").asText());
	}

}
