package com.contractar.microserviciousuario.repository.customrepositories;

import java.util.List;

public interface ProveedorVendibleCustomRepository {
	public List<?> getProveedorVendiblesInfo(Long proveedorId);
}
