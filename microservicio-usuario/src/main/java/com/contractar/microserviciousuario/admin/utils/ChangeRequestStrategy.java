package com.contractar.microserviciousuario.admin.utils;

import com.contractar.microserviciousuario.admin.models.ChangeRequest;
import com.contractar.microserviciousuario.admin.services.AdminService;

public interface ChangeRequestStrategy {
	public void run(ChangeRequest request, AdminService adminService);
}
