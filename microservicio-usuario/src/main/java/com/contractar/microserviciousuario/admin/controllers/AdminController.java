package com.contractar.microserviciousuario.admin.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.contractar.microserviciocommons.dto.usuario.sensibleinfo.UsuarioSensibleInfoDTO;
import com.contractar.microserviciousuario.admin.services.AdminService;
import com.contractar.microserviciousuario.admin.services.ChangeAlreadyRequestedException;
import com.contractar.microserviciousuario.admin.services.ChangeConfirmException;
import com.contractar.microservicioadapter.enums.PlanType;
import com.contractar.microserviciocommons.constants.controllers.AdminControllerUrls;

import jakarta.validation.Valid;

@RestController
public class AdminController {
	@Autowired
	private AdminService adminService;

	@PatchMapping(AdminControllerUrls.CHANGE_REQUEST_BY_ID)
	public ResponseEntity<?> confirmUserRequestChange(@PathVariable("id") Long id) throws ChangeConfirmException {
		adminService.confirmChangeRequest(id);
		return new ResponseEntity<>(HttpStatusCode.valueOf(200));
	}
	
	@GetMapping(AdminControllerUrls.CHANGE_REQUEST_BASE_URL)
	public ResponseEntity<?> requestChangeExists(@RequestParam(required = true, name="sourceTableId") Long sourceTableId,
			@RequestParam(required = true, name="searchAttributes") List<String> searchAttributes) {
		boolean requestExists = adminService.requestExists(sourceTableId, searchAttributes);
		return requestExists ? new ResponseEntity<>(HttpStatus.OK) :  new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@PutMapping(AdminControllerUrls.ADMIN_USUARIOS_BY_ID)
	public ResponseEntity<Void> updateUserCommonInfo(@RequestBody @Valid UsuarioSensibleInfoDTO body,
			@PathVariable("id") Long id) throws ChangeAlreadyRequestedException {
		try {
			adminService.addChangeRequestEntry(body, id);
			return new ResponseEntity<>(HttpStatusCode.valueOf(200));
		} catch (IllegalAccessException e) {
			return new ResponseEntity<>(HttpStatusCode.valueOf(409));
		}
	}

	@PutMapping(AdminControllerUrls.ADMIN_PROVEEDOR_BY_ID)
	public ResponseEntity<Void> updateProveedorPlan(@RequestBody PlanType plan, @PathVariable("id") Long proveedorId)
			throws ChangeAlreadyRequestedException {
		adminService.addChangeRequestEntry(plan, proveedorId);
		return new ResponseEntity<>(HttpStatusCode.valueOf(200));
	}
}
