package com.contractar.microserviciousuario.serialization;

import java.io.IOException;
import java.util.Iterator;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import com.contractar.microserviciocommons.constants.PriceType.PriceTypeValue;
import com.contractar.microserviciousuario.models.Producto;
import com.contractar.microserviciousuario.models.ProveedorVendible;
import com.contractar.microserviciousuario.models.Servicio;
import com.contractar.microserviciousuario.models.Vendible;
import com.contractar.microserviciousuario.models.VendibleCategory;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class VendibleDeserializer extends JsonDeserializer<Vendible> {

	@Override
	public Vendible deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
		ObjectNode node = p.getCodec().readTree(p);
		String vendibleType = node.get("vendibleType").asText();
		String nombre = node.get("nombre").asText();

		Vendible v = vendibleType.equals("servicios") ? new Servicio(nombre) : new Producto(nombre);

		Iterator<JsonNode> proveedoresVendiblesArray = node.get("proveedoresVendibles").elements();

		while (proveedoresVendiblesArray.hasNext()) {
			JsonNode current = proveedoresVendiblesArray.next();

			VendibleCategory category = new VendibleCategory(current.get("category").get("name").asText());
			String descripcion = current.get("descripcion").asText();
			int precio = current.get("precio").asInt();

			PriceTypeValue priceType = PriceTypeValue.valueOf(current.get("tipoPrecio").asText());
			String imagenUrl = current.get("imagenUrl").asText();

			double x = current.get("location").get("coordinates").get(0).asDouble();
			double y = current.get("location").get("coordinates").get(1).asDouble();
			Point location = new GeometryFactory().createPoint(new Coordinate(x, y));
			
			int stock = vendibleType.equals("servicios") ? 0 : current.get("stock").asInt();
			boolean offersDelivery = current.get("offersDelivery").asBoolean();
			boolean offersInCustomAddress = current.get("offersInCustomAddress").asBoolean();
			
			ProveedorVendible pv = new ProveedorVendible(null, precio, descripcion, imagenUrl, stock, null, null, priceType, category, offersDelivery);
			pv.setLocation(location);
			pv.setOffersInCustomAddress(offersInCustomAddress);
			
			v.getProveedoresVendibles().add(pv);
		}

		return v;
	}

}
