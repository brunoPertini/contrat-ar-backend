package com.contractar.microserviciousuario.models.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class LongListConverter implements AttributeConverter<List<Long>, String> {

    @Override
    public String convertToDatabaseColumn(List<Long> attribute) {
        return attribute != null ? attribute.stream()
            .map(String::valueOf)
            .collect(Collectors.joining(",")) : null;
    }

    @Override
    public List<Long> convertToEntityAttribute(String dbData) {
        return dbData != null && !dbData.isEmpty() ? Arrays.stream(dbData.split(","))
            .map(Long::valueOf)
            .collect(Collectors.toList()) : null;
    }
}

