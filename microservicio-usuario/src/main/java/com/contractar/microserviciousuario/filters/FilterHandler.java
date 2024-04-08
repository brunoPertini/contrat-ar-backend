package com.contractar.microserviciousuario.filters;

import java.util.Map;

public interface FilterHandler {
	public void setNextHanlder(FilterHandler handler);
	
	public boolean handleRequest(Map<String, Object> args);
}
