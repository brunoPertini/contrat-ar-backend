package com.contractar.microserviciocommons.serialization;

import java.io.IOException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class YearMonthDeserializer extends JsonDeserializer<YearMonth> {
	  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM-yyyy");

	    @Override
	    public YearMonth deserialize(JsonParser parser, DeserializationContext context) throws IOException {
	        return YearMonth.parse(parser.getText(), FORMATTER);
	    }
}
