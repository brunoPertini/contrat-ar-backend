package com.contractar.microserviciousuario.admin.utils;

import java.lang.reflect.InvocationTargetException;

import com.contractar.microserviciocommons.exceptions.vendibles.VendibleNotFoundException;
import com.contractar.microserviciousuario.admin.models.ChangeRequest;
import com.contractar.microserviciousuario.admin.services.AdminService;

public interface ChangeRequestDenyStrategy {
	public void run(ChangeRequest request, AdminService adminService) throws VendibleNotFoundException,
			ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException;
}
