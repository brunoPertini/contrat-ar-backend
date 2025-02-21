package com.contractar.microserviciocommons.constants.controllers;

public final class AdminControllerUrls {
	
	public static final String USUARIOS_BASE_URL = "/admin/usuarios";
	
	public static final String CHANGE_REQUEST_BASE_URL = "/admin/change-requests";
	
	public static final String CHANGE_REQUEST_BY_ID = CHANGE_REQUEST_BASE_URL+"/{id}";
	
	public static final String ADMIN_USUARIOS_BY_ID = "/admin/usuarios/{id}";
	
	public static final String ADMIN_USUARIOS_SENSIBLE_INFO = ADMIN_USUARIOS_BY_ID + "/security";
		
	public static final String ADMIN_PROVEEDORES_BY_ID = "/admin/proveedores/{id}";
	
	public static final String ADMIN_USER = "/admin/change-requests/usuarios";
	
	public static final String ADMIN_PROVEEDOR = "/admin/change-requests/usuarios/proveedor";
	
	public static final String ADMIN_POST_BY_ID = ADMIN_PROVEEDORES_BY_ID + "/vendible/{vendibleId}";
	
	public static final String ADMIN_PROVEEDOR_SUBSCRIPTION_PLAN_CHANGE = ADMIN_PROVEEDORES_BY_ID + "/plan/{planId}";
}
