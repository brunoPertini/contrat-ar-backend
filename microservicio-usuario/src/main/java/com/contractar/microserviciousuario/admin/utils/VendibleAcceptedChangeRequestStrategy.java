package com.contractar.microserviciousuario.admin.utils;

import com.contractar.microserviciocommons.constants.controllers.UsersControllerUrls;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleNotFoundException;
import com.contractar.microserviciocommons.mailing.VendibleModificationNotification;
import com.contractar.microserviciousuario.admin.models.ChangeRequest;
import com.contractar.microserviciousuario.admin.services.AdminService;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.models.ProveedorVendible;
import com.contractar.microserviciousuario.models.ProveedorVendibleId;

public class VendibleAcceptedChangeRequestStrategy implements ChangeRequestStrategy {

	@Override
	public void run(ChangeRequest request, AdminService adminService) {
		Long proveedorId = request.getSourceTableIds().get(0);
		Long vendibleId = request.getSourceTableIds().get(1);
		

		try {
			ProveedorVendible post = adminService.findPost(new ProveedorVendibleId(proveedorId, vendibleId));
			Proveedor linkedUser = post.getProveedor(); 
			
			VendibleModificationNotification mailBody = new VendibleModificationNotification(linkedUser.getEmail(),
					true, linkedUser.getName(), post.getVendible().getNombre());
			
			adminService.sendEmail(UsersControllerUrls.POST_RESULT_NOTIFICATION, mailBody);
			
		} catch (IllegalArgumentException | VendibleNotFoundException e) {
			throw new RuntimeException(e);
		}

	}

}
