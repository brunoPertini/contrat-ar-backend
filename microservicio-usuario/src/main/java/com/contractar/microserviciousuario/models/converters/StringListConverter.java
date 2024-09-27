package com.contractar.microserviciousuario.models.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.List;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        return attribute != null ? String.join(",", attribute) : null;
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        return dbData != null && !dbData.isEmpty() ? Arrays.asList(dbData.split(",")) : null;
    }
}

