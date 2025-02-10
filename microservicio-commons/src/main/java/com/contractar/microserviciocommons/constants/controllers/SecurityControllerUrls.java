package com.contractar.microserviciocommons.constants.controllers;

public final class SecurityControllerUrls {
	
	public static final String TOKEN_BASE_PATH = "/oauth/token";

	public static final String GET_USER_PAYLOAD_FROM_TOKEN = TOKEN_BASE_PATH + "/payload";
	
	public static final String GET_PUBLIC_KEY = "/oauth/public_key";
	
	public static final String GET_TOKEN_FOR_LINK = TOKEN_BASE_PATH + "/link";
	
	public static final String GET_TOKEN_FOR_NEW_USER = TOKEN_BASE_PATH + "/user";
	
	public static final String SEND_2FA_MAIL = "/security/2fa";
	
	public static final String SEND_2FA_MAIL_CONFIRM = SEND_2FA_MAIL + "/{code}";

}
