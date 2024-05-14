package com.contractar.microserviciocommons.constants.controllers;

public final class ImagenesControllerUrls {
	
	public static final String IMAGE_BASE_URL = "/image";
	
	public static final String UPLOAD_VENDIBLE_IMAGE_URL = IMAGE_BASE_URL + "/vendible/{vendibleName}/proveedor/{proveedorId}/upload";
	
	public static final String UPLOAD_PROVEEDOR_PHOTO_URL = IMAGE_BASE_URL + "/proveedor/{proveedorId}/upload";
	
}
