package com.contractar.microserviciocommons.constants.controllers;

public final class PromotionControllerUrls {
	private PromotionControllerUrls () {}
	
	public static final String PROMOTION_BASE_URL = "/promotion";
	
	public static final String PROMOTION_BY_ID = "/{userId}";
	
	public static final String PROMOTION_INSTANCE_BASE_URL = "/instance";
	
	public static final String PROMOTION_INSTANCE_BY_ID = PROMOTION_INSTANCE_BASE_URL + "/{suscriptionId}";
}
