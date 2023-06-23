package com.contractar.microserviciocommons.config;

import com.contractar.microserviciocommons.infra.ErrorDetails;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class ErrorDetailsSerializer extends StdSerializer<ErrorDetails> {

	private static final long serialVersionUID = 1137549160545882375L;

	public ErrorDetailsSerializer() {
		this(null);
	}

	protected ErrorDetailsSerializer(Class<ErrorDetails> t) {
		super(t);
	}

	@Override
	public void serialize(ErrorDetails errorDetails, JsonGenerator generator, SerializerProvider provider)
			throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		generator.writeStartObject();
		generator.writeStringField("timestamp", objectMapper.writeValueAsString(errorDetails.getTimestamp()));
		generator.writeNumberField("statusCode", errorDetails.getStatus());
		generator.writeStringField("message", errorDetails.getError());
		generator.writeEndObject();
	}
}
