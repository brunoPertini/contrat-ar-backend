package com.contractar.microserviciocommons.helpers;

public final class StringHelper {
	
	private StringHelper() {}

	public static String toUpperCamelCase(String sourceWord) {
		return Character.toUpperCase(sourceWord.charAt(0)) + sourceWord.substring(1).toLowerCase();
	}

}