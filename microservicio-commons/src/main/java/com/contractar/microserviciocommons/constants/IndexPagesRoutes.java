package com.contractar.microserviciocommons.constants;

import java.util.Map;

public final class IndexPagesRoutes {
	public static final String ROLE_CLIENTE = "/cliente";
	
	public static final String DEFAULT = "/";
	
	public static Map<String,String> getAllRoutes() {
		return Map.of("DEFAULT", IndexPagesRoutes.DEFAULT,
					  "ROLE_CLIENTE", IndexPagesRoutes.ROLE_CLIENTE);
	}
}

