package com.contractar.microserviciocommons.usuarios;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

import java.io.IOException;

public class UbicacionSerializer extends StdSerializer<Point> {

    private static final long serialVersionUID = 7178275182513898923L;

	protected UbicacionSerializer(Class<Point> t) {
		super(t);
	}

	public UbicacionSerializer() {
        this(null);
    }

    @Override
    public void serialize(Point point, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("coordinates");
        serializeCoordinates(point.getCoordinate(), jsonGenerator);
        jsonGenerator.writeEndObject();
    }

    private void serializeCoordinates(Coordinate coordinate, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeStartArray();
        jsonGenerator.writeNumber(coordinate.getX());
        jsonGenerator.writeNumber(coordinate.getY());
        if (!Double.isNaN(coordinate.getZ())) {
            jsonGenerator.writeNumber(coordinate.getZ());
        }
        jsonGenerator.writeEndArray();
    }
}
