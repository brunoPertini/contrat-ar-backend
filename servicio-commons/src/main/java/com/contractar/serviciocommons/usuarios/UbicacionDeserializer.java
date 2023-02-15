package com.contractar.serviciocommons.usuarios;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;

public class UbicacionDeserializer extends StdDeserializer<Point> {

    public UbicacionDeserializer() {
        super(Point.class); 
    }

    @Override
    public Point deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        double x = Double.parseDouble(node.get("x").textValue());
        double y = Double.parseDouble(node.get("y").textValue());        
        return new GeometryFactory().createPoint(new Coordinate(x, y));
    }
}
