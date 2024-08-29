package com.contractar.microserviciocommons.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.fasterxml.jackson.databind.BeanProperty;
import com.contractar.microserviciocommons.dto.WithDatePatternDTO;

/**
 * This serializer should be used for every LocalDate that wants to perform formatting with a previously
 * set pattern exposed from {@code} MessageConfig
 */
public class DynamicPatternDateSerializer extends JsonSerializer<LocalDate> implements ContextualSerializer {

    private String pattern;

    public DynamicPatternDateSerializer() {
        this(null);
    }

    public DynamicPatternDateSerializer(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public void serialize(LocalDate date, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String actualPattern = (pattern != null) ? pattern : "yyyy-MM-dd"; // Patrón por defecto
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(actualPattern);
        gen.writeString(date.format(formatter));
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property) {
        if (property != null) {
            WithDatePatternDTO dto = (WithDatePatternDTO) property.getMember().getDeclaringClass().cast(property.getMember().getAnnotated());
            return new DynamicPatternDateSerializer(dto.getDatePattern());
        }
        return this;
    }
}
