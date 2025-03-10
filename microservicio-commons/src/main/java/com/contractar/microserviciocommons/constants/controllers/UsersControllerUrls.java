package com.contractar.microserviciocommons.constants.controllers;

public final class UsersControllerUrls {
	
	public static final String PROVEEDOR_VENDIBLE = "/usuarios/proveedor/{proveedorId}/vendible/{vendibleId}";

	public static final String UPDATE_PROVEEDOR = "/usuarios/proveedor";
	
	public static final String CREATE_PROVEEDOR = "/usuarios/proveedor";
	
	public static final String GET_PROVEEDOR = "/usuarios/proveedor";
	
	public static final String GET_USUARIOS = "/usuarios";
	
	public static final String GET_USUARIO_INFO = "/usuarios/{userId}/info";
	
	public static final String GET_USUARIO_FIELD = "/usuarios/{userId}/field/{fieldName}";
	
	public static final String USUARIO_BASE_URL = "/usuarios/{usuarioId}";
	
	public static final String CREATE_CLIENTE = "/usuarios/cliente";
	
	public static final String SEND_REGISTRATION_LINK_EMAIL = "/mail/signup/link";
	
	public static final String SIGNUP_OK_EMAIL = "/mail/signup/ok";
	
	public static final String FORGOT_PASSWORD_EMAIL = "/mail/password/forgot";
}
