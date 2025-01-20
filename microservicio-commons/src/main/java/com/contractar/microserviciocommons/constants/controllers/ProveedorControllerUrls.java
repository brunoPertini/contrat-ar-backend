package com.contractar.microserviciocommons.constants.controllers;

public final class ProveedorControllerUrls {
	public static final String PROVEEDOR_BASE_URL = "/proveedor/{proveedorId}";
	public static final String INTERNAL_PLAN_BASE_URL = "/internal/plan";
	public static final String GET_SUSCRIPCION = "/suscripcion/{suscriptionId}";
	public static final String GET_PROVEEDOR_SUSCRIPCION = PROVEEDOR_BASE_URL + "/suscripcion";
	public static final String POST_PROVEEDOR_SUSCRIPCION = GET_PROVEEDOR_SUSCRIPCION + "/{planId}";
	public static final String GET_VENDIBLES_OF_PROVEEDOR = PROVEEDOR_BASE_URL + "/vendible";
}
