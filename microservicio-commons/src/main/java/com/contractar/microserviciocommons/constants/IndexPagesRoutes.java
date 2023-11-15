package com.contractar.microserviciocommons.constants;

import java.util.Map;

public final class IndexPagesRoutes {
	public static final String CLIENTE = "/cliente";
	
	public static final String PROVEEDOR_SERVICIOS = "/proveedor";
	
	public static final String PROVEEDOR_PRODUCTOS = "/proveedor";
	
	public static final String DEFAULT = "/";
	
	public static Map<String,String> getAllRoutes() {
		return Map.of("DEFAULT", IndexPagesRoutes.DEFAULT,
					  "CLIENTE", IndexPagesRoutes.CLIENTE,
					  "PROVEEDOR_SERVICIOS", IndexPagesRoutes.PROVEEDOR_SERVICIOS,
					  "PROVEEDOR_PRODUCTOS", IndexPagesRoutes.PROVEEDOR_PRODUCTOS);
	}
}

