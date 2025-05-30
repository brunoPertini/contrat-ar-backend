package com.contractar.microserviciousuario.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.contractar.microserviciousuario.admin.dtos.UsuarioPersonalDataUpdateDTO;
import com.contractar.microserviciousuario.admin.services.ChangeConfirmException;
import com.contractar.microserviciousuario.models.Cliente;
import com.contractar.microserviciousuario.services.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;

import com.contractar.microserviciocommons.constants.controllers.ClienteControllerUrls;
import com.contractar.microserviciocommons.dto.usuario.UsuarioDTO;
import com.contractar.microserviciocommons.exceptions.CantUpdateUserException;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;

@RestController
public class ClienteController {
	@Autowired private UsuarioService usuarioService;
	
	@PutMapping(ClienteControllerUrls.CLIENTE_BASE_URL)
	public ResponseEntity<?> update(@RequestBody UsuarioPersonalDataUpdateDTO body, @PathVariable Long id, HttpServletRequest request) 
			throws CantUpdateUserException,
					UserNotFoundException,
					ChangeConfirmException {
		String parsedJwt = request.getHeader("Authorization").replace("Bearer ", "");
		Cliente updated = usuarioService.updateCliente(id, body, parsedJwt);
		return new ResponseEntity<>(new UsuarioDTO(
				updated.getId(),
				updated.getName(),
				updated.getSurname(), 
				updated.getEmail(),
				updated.isActive(),
				updated.getBirthDate(),
				updated.getLocation(),
				updated.getPhone()), HttpStatus.OK);
	}
}
