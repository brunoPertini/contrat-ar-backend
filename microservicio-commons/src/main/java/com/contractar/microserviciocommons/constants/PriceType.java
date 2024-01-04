package com.contractar.microserviciocommons.constants;

public final class PriceType {
	public static final String FIXED = "Fijo";
	
	public static final String VARIABLE = "Variable";
	
	public static final String VARIABLE_WITH_AMOUNT = "Variable con monto minimo";
	
	public enum PriceTypeValue {
		FIXED,
		VARIABLE,
		VARIABLE_WITH_AMOUNT,
	}
}
