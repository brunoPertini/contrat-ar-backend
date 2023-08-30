package com.contractar.microserviciocommons.constants;

import java.util.Map;

public final class IndexPagesRoutes {
	public static final String CLIENTE = "/cliente";
	
	public static final String DEFAULT = "/";
	
	public static Map<String,String> getAllRoutes() {
		return Map.of("DEFAULT", IndexPagesRoutes.DEFAULT,
					  "CLIENTE", IndexPagesRoutes.CLIENTE);
	}
}

