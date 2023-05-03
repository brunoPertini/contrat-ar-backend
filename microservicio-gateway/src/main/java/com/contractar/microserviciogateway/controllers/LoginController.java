package com.contractar.microserviciogateway.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.contractar.microserviciogateway.security.helpers.JwtHelper;

@RestController
public class LoginController {
    private final JwtHelper jwtHelper;
	private final UserDetailsService userDetailsService;
	private final PasswordEncoder passwordEncoder;
	
	public LoginController(JwtHelper jwtHelper, UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
		this.jwtHelper = jwtHelper;
		this.userDetailsService = userDetailsService;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        UserDetails userDetails;
        try {
			userDetails = userDetailsService.loadUserByUsername(email);
		} catch (UsernameNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
		}
		
		if (passwordEncoder.matches(password, userDetails.getPassword())) {
			Map<String, String> claims = new HashMap<>();
			claims.put("email", email);
			
			String authorities = userDetails.getAuthorities().stream()
					.map(GrantedAuthority::getAuthority)
					.collect(Collectors.joining(","));
			claims.put("authorities", authorities);
			claims.put("userId", String.valueOf(1));
			
			String jwt = jwtHelper.createJwtForClaims(email, claims);
			return new ResponseEntity<String>(jwt, HttpStatus.OK);
		}
		
		throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
	}
}
