package com.contractar.microserviciocommons.constants.controllers;

public class VendiblesControllersUrls {
	
	public static final String INTERNAL_GET_VENDIBLE = "/internal/vendible";
	
	public static final String INTERNAL_POST_BY_ID = INTERNAL_GET_VENDIBLE + "/{vendibleId}/proveedor/{proveedorId}";
	
	public static final String GET_VENDIBLE = "/vendible";
	
	public static final String GET_SERVICE = "/service";
	
	public static final String SAVE_SERVICE = "/service";
	
	public static final String GET_PRODUCT = "/product";
	
	public static final String SAVE_PRODUCT = "/product";
	
	public static final String MODIFY_PRODUCT = "/product/{vendibleId}";
	
	public static final String MODIFY_SERVICE = "/service/{vendibleId}";
		
	public static final String DELETE_VENDIBLE = "/vendible/{vendibleId}";
	
	public static final String GET_VENDIBLE_TYPE = "/vendible/{vendibleId}/vendible_type";
	
	public static final String GET_CATEGORY_HIERACHY = "/vendible/category";
	
	public static final String GET_VENDIBLE_POSTS = "/vendible/{vendibleId}/proveedores";
	
	public static final String GET_VENDIBLE_POSTS_V2 = "/v2/vendible/{vendibleId}/proveedores";

}
