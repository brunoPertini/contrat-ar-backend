package com.contractar.microserviciocommons.serialization;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import com.contractar.microserviciocommons.dto.SuscripcionDTO;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class SuscripcionDTOSerializer extends JsonSerializer<SuscripcionDTO> {

	@Override
	public void serialize(SuscripcionDTO dto, JsonGenerator generator, SerializerProvider serializers) throws IOException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dto.getDatePattern());
	        
		generator.writeStartObject();
		generator.writeNumberField("id", dto.getId());
		generator.writeBooleanField("isActive", dto.isActive());
		generator.writeNumberField("usuarioId", dto.getUsuarioId());
		generator.writeNumberField("planId", dto.getPlanId());
		generator.writeNumberField("planPrice", dto.getPlanPrice());
		generator.writeStringField("createdDate", dto.getCreatedDate().format(formatter));
		generator.writeEndObject();
	}

}
