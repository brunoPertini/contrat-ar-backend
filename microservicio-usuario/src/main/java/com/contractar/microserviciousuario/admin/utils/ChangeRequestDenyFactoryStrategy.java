package com.contractar.microserviciousuario.admin.utils;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.contractar.microserviciousuario.admin.models.ChangeRequest;

public final class ChangeRequestDenyFactoryStrategy {
	private static final String POST_ENTITY = "proveedor_vendible";

	public static ChangeRequestDenyStrategy create(ChangeRequest request) {
		Map<String, Supplier<ChangeRequestDenyStrategy>> creators = Map.of(POST_ENTITY,
				VendibleChangeRequestStrategy::new);
		
		return Optional.ofNullable(creators.get(request.getSourceTable()))
				.map(s -> s.get())
				.orElse(null);
	}
}
