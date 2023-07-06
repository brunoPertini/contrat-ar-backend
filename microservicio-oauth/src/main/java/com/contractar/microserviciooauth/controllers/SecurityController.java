package com.contractar.microserviciooauth.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciooauth.services.UserDetailsServiceImpl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class SecurityController {

	private UserDetailsService userDetailsService;

	public SecurityController(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@GetMapping("/oauth/login")
	public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password)
			throws UserNotFoundException {
		UserDetails userDetails = userDetailsService.loadUserByUsername(email);

		String jwt = ((UserDetailsServiceImpl)userDetailsService).createJwtForUser(email, password, userDetails);
		
        Cookie cookie = new Cookie("t", jwt);
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        cookie.setHttpOnly(true);

        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        response.addCookie(cookie);
       
		return new ResponseEntity<String>(jwt, HttpStatus.OK);			
	}
}
