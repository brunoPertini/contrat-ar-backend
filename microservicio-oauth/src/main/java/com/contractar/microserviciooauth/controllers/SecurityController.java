package com.contractar.microserviciooauth.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciooauth.services.UserDetailsServiceImpl;
import com.contractar.microserviciousuario.models.Usuario;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class SecurityController {

	private UserDetailsService userDetailsService;
	
	@Value("classpath:clave_publica.pem")
	Resource clavePublicaResource;

	public SecurityController(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@GetMapping("/oauth/login")
	public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password)
			throws UserNotFoundException {
		Usuario userDetails = (Usuario) userDetailsService.loadUserByUsername(email);
				
		String jwt = ((UserDetailsServiceImpl)userDetailsService).createJwtForUser(email, password, userDetails);
		
        Cookie cookie = new Cookie("t", jwt);
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        cookie.setHttpOnly(true);

        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        response.addCookie(cookie);
       
		return new ResponseEntity<String>(jwt, HttpStatus.OK);			
	}
	
	@GetMapping("/oauth/public_key")
	public ResponseEntity<String> getPublicKey() {
		try (InputStream inputStream = clavePublicaResource.getInputStream();
	             Scanner scanner = new Scanner(inputStream, "UTF-8")) {

	            StringBuilder content = new StringBuilder();
	            while (scanner.hasNextLine()) {
	                String line = scanner.nextLine();
	                content.append(line).append(System.lineSeparator());
	            }

	            String publicKey = content.toString();
	            return ResponseEntity.ok(publicKey);
	        } catch (IOException e) {
				return ResponseEntity.ok("");
			}
	}
}
