package com.contractar.microserviciousuario.admin.controllers;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.contractar.microserviciocommons.dto.UsuarioFiltersDTO;
import com.contractar.microserviciocommons.dto.usuario.sensibleinfo.UsuarioSensibleInfoDTO;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleNotFoundException;
import com.contractar.microserviciousuario.admin.dtos.ProveedorPersonalDataUpdateDTO;
import com.contractar.microserviciousuario.admin.dtos.ProveedorVendibleAdminDTO;
import com.contractar.microserviciousuario.admin.dtos.UsuarioPersonalDataUpdateDTO;
import com.contractar.microserviciousuario.admin.services.AdminService;
import com.contractar.microserviciousuario.admin.services.ChangeAlreadyRequestedException;
import com.contractar.microserviciousuario.admin.services.ChangeConfirmException;
import com.contractar.microserviciocommons.constants.controllers.AdminControllerUrls;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
public class AdminController {
	@Autowired
	private AdminService adminService;

	public static enum UsuariosTypeFilter {
		proveedores, clientes,
	};

	@PatchMapping(AdminControllerUrls.CHANGE_REQUEST_BY_ID)
	public ResponseEntity<?> confirmUserRequestChange(@PathVariable("id") Long id) throws ChangeConfirmException {
		adminService.confirmChangeRequest(id);
		return new ResponseEntity<>(HttpStatusCode.valueOf(200));
	}

	@GetMapping(AdminControllerUrls.CHANGE_REQUEST_BASE_URL)
	public ResponseEntity<?> requestChangeExists(
			@RequestParam(required = true, name = "sourceTableId") Long sourceTableId,
			@RequestParam(required = true, name = "searchAttributes") List<String> searchAttributes) {
		boolean requestExists = adminService.requestExists(sourceTableId, searchAttributes);
		return requestExists ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@PutMapping(AdminControllerUrls.ADMIN_USUARIOS_BY_ID)
	public ResponseEntity<Void> updateUserCommonInfo(@RequestBody @Valid UsuarioSensibleInfoDTO body,
			@PathVariable("id") Long id) throws ChangeAlreadyRequestedException {

		try {
			adminService.addChangeRequestEntry(body, List.of(id.toString()));
			return new ResponseEntity<>(HttpStatusCode.valueOf(200));
		} catch (IllegalAccessException e) {
			return new ResponseEntity<>(HttpStatusCode.valueOf(409));
		}
	}

	@PutMapping(AdminControllerUrls.ADMIN_PROVEEDOR_SUBSCRIPTION_PLAN_CHANGE)
	public ResponseEntity<Void> updateProveedorPlan(@PathVariable("proveedorId") Long proveedorId, @PathVariable("planId") Long newPlanId)
			throws ChangeAlreadyRequestedException, ChangeConfirmException {
		adminService.addChangeRequestEntry(proveedorId, newPlanId);
		return new ResponseEntity<>(HttpStatusCode.valueOf(200));
	}
	
	@PatchMapping(AdminControllerUrls.ADMIN_PROVEEDORES_BY_ID)
	public ResponseEntity<Void> updateProveedor (@PathVariable("id")Long userId, @RequestBody @Valid ProveedorPersonalDataUpdateDTO body) throws ClassNotFoundException,
	IllegalAccessException, InvocationTargetException {
		adminService.updateProveedorPersonalData(userId, body);
		return new ResponseEntity<>(HttpStatusCode.valueOf(200));
	}
		
	@PatchMapping(AdminControllerUrls.ADMIN_USUARIOS_BY_ID)
	public ResponseEntity<Void> updateProveedor (@PathVariable("id")Long userId, @RequestBody @Valid UsuarioPersonalDataUpdateDTO body) throws ClassNotFoundException,
	IllegalAccessException, InvocationTargetException {
		adminService.updateClientePersonalData(userId, body);
		return new ResponseEntity<>(HttpStatusCode.valueOf(200));
	}
	
	
	  @PutMapping(AdminControllerUrls.ADMIN_POST_BY_ID) 
	  public ResponseEntity<?> updatePost(@PathVariable(name = "id") Long proveedorId,
			 @PathVariable(name = "vendibleId") Long
	  vendibleId, @RequestBody ProveedorVendibleAdminDTO body,
	  HttpServletRequest request) throws IllegalAccessException,
	  ChangeAlreadyRequestedException,
	  VendibleNotFoundException,
	  ClassNotFoundException,
	  IllegalArgumentException,
	  InvocationTargetException {
		  	adminService.updatePostAdmin(body, proveedorId, vendibleId, request);
			return new ResponseEntity<>(HttpStatusCode.valueOf(200));	
	  }
	 

	@PostMapping(AdminControllerUrls.USUARIOS_BASE_URL)
	public ResponseEntity<?> getUsuarios(@RequestParam(name = "type", required = true) UsuariosTypeFilter usuarioType,
			@RequestParam(name = "plan", required = false) Long planId,
			@RequestParam(name = "showOnlyActives", required=false) Boolean onlyActives,
			@RequestBody UsuarioFiltersDTO filters) throws IllegalAccessException {
		return new ResponseEntity<>(adminService.getAllFilteredUsuarios(usuarioType.toString(), filters, onlyActives, planId), HttpStatusCode.valueOf(200));
	}
	
	@DeleteMapping(AdminControllerUrls.ADMIN_USUARIOS_BY_ID)
	public ResponseEntity<Void> deleteUsuario(@PathVariable("id") Long userId)  throws UserNotFoundException {
		adminService.deleteUser(userId);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
}
