package com.contractar.microserviciooauth.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciooauth.services.UserDetailsServiceImpl;

@RestController
public class SecurityController {

	private UserDetailsService userDetailsService;

	public SecurityController(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@GetMapping("/oauth/login")
	public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) throws UserNotFoundException {
		UserDetails userDetails = userDetailsService.loadUserByUsername(email);

		String jwt = ((UserDetailsServiceImpl)userDetailsService).createJwtForUser(email, password, userDetails);
		return new ResponseEntity<String>(jwt, HttpStatus.OK);			
	}
}
