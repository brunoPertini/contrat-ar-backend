package com.contractar.microserviciooauth.controllers;

import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.contractar.microserviciooauth.helpers.JwtHelper;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;

@RestController
public class SecurityController {
	@Autowired
	private RSAPublicKey storePublicKey;
	
	@Autowired
	private PrivateKey storePrivateKey;

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
	public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
		UserDetails userDetails;
		try {
			userDetails = userDetailsService.loadUserByUsername(email);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
		}

		if (passwordEncoder.matches(password, userDetails.getPassword())) {
			Map<String, String> claims = new HashMap<>();
			claims.put("email", email);

			String authorities = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
					.collect(Collectors.joining(","));
			claims.put("authorities", authorities);
			claims.put("userId", String.valueOf(1));

			String jwt = jwtHelper.createJwtForClaims(email, claims);
			return new ResponseEntity<String>(jwt, HttpStatus.OK);
		}

		throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
	}

	@GetMapping("/.well-known/openid-configuration/.well-known/oauth-authorization-server/json/keys.json")
	public ResponseEntity<JWKSet> keys() {
		 RSAPrivateKey privateKey = (RSAPrivateKey) storePrivateKey;
		 RSAPublicKey publicKey = (RSAPublicKey) storePublicKey;

	     JWK jwk = new RSAKey.Builder(publicKey)
	                .privateKey(privateKey)
	                .keyUse(KeyUse.SIGNATURE)
	                .algorithm(JWSAlgorithm.RS256)
	                .keyID("1")
	                .build();

	     JWKSet jwkSet = new JWKSet(jwk);
	     
	     return ResponseEntity.ok(jwkSet);
	}
}
