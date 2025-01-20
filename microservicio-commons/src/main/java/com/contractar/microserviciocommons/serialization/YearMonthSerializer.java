package com.contractar.microserviciocommons.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class YearMonthSerializer extends JsonSerializer<YearMonth> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM-yyyy");

    @Override
    public void serialize(YearMonth value, JsonGenerator generator, SerializerProvider serializers) throws IOException {
        generator.writeString(value.format(FORMATTER));
    }
}
