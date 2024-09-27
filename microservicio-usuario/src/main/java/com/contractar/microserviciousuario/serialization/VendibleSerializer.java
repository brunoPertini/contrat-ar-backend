package com.contractar.microserviciousuario.serialization;

import java.io.IOException;

import com.contractar.microserviciousuario.models.Vendible;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class VendibleSerializer extends JsonSerializer<Vendible> {

	@Override
	public void serialize(Vendible vendible, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeStartObject();
		gen.writeStringField("vendibleType", vendible.getVendibleType());
		gen.writeStringField("nombre", vendible.getNombre());
		gen.writeArrayFieldStart("proveedoresVendibles");
		gen.writeEndArray();
		gen.writeEndObject();

	}

}
