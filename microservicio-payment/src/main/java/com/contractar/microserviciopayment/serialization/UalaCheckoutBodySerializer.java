package com.contractar.microserviciopayment.serialization;

import java.io.IOException;
import java.util.Optional;

import com.contractar.microserviciopayment.providers.uala.CheckoutBody;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class UalaCheckoutBodySerializer extends JsonSerializer<CheckoutBody> {

	@Override
	public void serialize(CheckoutBody value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeStartObject();
		gen.writeStringField("amount", String.valueOf(value.getAmount()));
		gen.writeStringField("description", value.getDescription());
		gen.writeStringField("userName", value.getUserName());
		gen.writeStringField("callback_fail", value.getCallbackFail());
		gen.writeStringField("callback_success", value.getCallbackSuccess());
		
		Optional.ofNullable(value.getNotificationUrl()).ifPresent(notificationUrl -> {
			try {
				gen.writeStringField("notification_url", notificationUrl);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
		gen.writeEndObject();
	}

}
