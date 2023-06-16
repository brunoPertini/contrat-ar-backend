package com.contractar.microserviciooauth.controllers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciooauth.helpers.JwtHelper;

@RestController
public class SecurityController {
	private final JwtHelper jwtHelper;
	private final UserDetailsService userDetailsService;
	private final PasswordEncoder passwordEncoder;

	public SecurityController(JwtHelper jwtHelper, UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
		this.jwtHelper = jwtHelper;
		this.userDetailsService = userDetailsService;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping("/oauth/login")
	public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) throws UserNotFoundException {
		UserDetails userDetails = userDetailsService.loadUserByUsername(email);

		if (passwordEncoder.matches(password, userDetails.getPassword())) {
			Map<String, Object> claims = new HashMap<>();
			claims.put("email", email);

			Collection<String> authorities = userDetails
					.getAuthorities()
					.stream()
					.map(GrantedAuthority::getAuthority)
					.collect(Collectors.toList());
			
			claims.put("authorities", authorities.toArray(new String[0]));
			claims.put("userId", String.valueOf(1));

			String jwt = jwtHelper.createJwtForClaims(email, claims);
			return new ResponseEntity<String>(jwt, HttpStatus.OK);
		}
		
		throw new UserNotFoundException();

	}
}
