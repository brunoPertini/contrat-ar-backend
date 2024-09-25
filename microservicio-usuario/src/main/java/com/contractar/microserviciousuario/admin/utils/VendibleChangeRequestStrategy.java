package com.contractar.microserviciousuario.admin.utils;

import java.lang.reflect.InvocationTargetException;

import com.contractar.microservicioadapter.enums.PostState;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleNotFoundException;
import com.contractar.microserviciousuario.admin.dtos.ProveedorVendibleAdminDTO;
import com.contractar.microserviciousuario.admin.models.ChangeRequest;
import com.contractar.microserviciousuario.admin.services.AdminService;
import com.contractar.microserviciousuario.models.ProveedorVendible;
import com.contractar.microserviciousuario.models.ProveedorVendibleId;

public class VendibleChangeRequestStrategy implements ChangeRequestDenyStrategy {

	@Override
	public void run(ChangeRequest request, AdminService adminService) throws VendibleNotFoundException,
			ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Long proveedorId = request.getSourceTableIds().get(0);
		Long vendibleId = request.getSourceTableIds().get(1);

		ProveedorVendible post = adminService.findPost(new ProveedorVendibleId(proveedorId, vendibleId));
		ProveedorVendibleAdminDTO stateChanged = new ProveedorVendibleAdminDTO();
		stateChanged.setState(PostState.REJECTED);

		adminService.performPostUpdate(post, stateChanged);
		adminService.deleteChangeRequest(request.getId());

	}

}
