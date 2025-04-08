package com.contractar.microserviciousuario.admin.utils;

public final class ChangeRequestFactoryStrategy {	
	private ChangeRequestFactoryStrategy() {}

	public static ChangeRequestStrategy createPostRejectedStrategy() {
		return new VendibleRejectedChangeRequestStrategy();
	}
	
	public static ChangeRequestStrategy createUserAcceptedStrategy() {
		return new UsuarioActiveAcceptChangeRequestStrategy();
	}
	
	public static ChangeRequestStrategy createUserRejectedStrategy() {
		return new UsuarioActiveRejectChangeRequestStrategy();
	}
}
