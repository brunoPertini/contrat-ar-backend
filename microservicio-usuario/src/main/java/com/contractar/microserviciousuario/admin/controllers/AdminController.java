package com.contractar.microserviciousuario.admin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.contractar.microserviciocommons.dto.usuario.UsuarioSensibleInfoDTO;
import com.contractar.microserviciousuario.admin.services.AdminService;
import com.contractar.microserviciousuario.admin.services.ChangeAlreadyRequestedException;

import jakarta.validation.Valid;

@RestController
public class AdminController {
	@Autowired
	private AdminService adminService;
	
	@PatchMapping("/admin/change-requests/{id}")
	public ResponseEntity<Void> confirmUserRequestChange(@PathVariable("id") Long id) {
		
	}

	@PutMapping("/admin/usuarios/{id}")
	public ResponseEntity<Void> updateUserCommonInfo(@RequestBody @Valid UsuarioSensibleInfoDTO body, @PathVariable("id") Long id) throws ChangeAlreadyRequestedException {
		try {
			adminService.addChangeRequestEntry(body, id);
			return new ResponseEntity<>(HttpStatusCode.valueOf(200));
		} catch (IllegalAccessException e) {
			return new ResponseEntity<>(HttpStatusCode.valueOf(409));
		}
	}
}
