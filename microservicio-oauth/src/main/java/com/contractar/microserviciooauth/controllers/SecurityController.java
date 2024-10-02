package com.contractar.microserviciooauth.controllers;

import java.security.interfaces.RSAPublicKey;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.contractar.microserviciocommons.constants.controllers.SecurityControllerUrls;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciooauth.helpers.JwtHelper;
import com.contractar.microserviciooauth.services.UserDetailsServiceImpl;
import com.contractar.microserviciousuario.dtos.UsuarioOauthDTO;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class SecurityController {

	private UserDetailsService userDetailsService;

	@Autowired
	private RSAPublicKey storePublicKey;

	@Autowired
	private JwtHelper jwtHelper;

	public SecurityController(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@GetMapping("/oauth/login")
	public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password)
			throws UserNotFoundException {
		UsuarioOauthDTO userDetails = (UsuarioOauthDTO) userDetailsService.loadUserByUsername(email);

		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(userDetails.getRole().getNombre());
		List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>(
				Collections.singletonList(authority));

		String jwt = ((UserDetailsServiceImpl) userDetailsService).createJwtForUser(email, password, userDetails,
				authorities);

		ZoneId zone = ZoneId.of(ZoneId.SHORT_IDS.get("AGT"));
		ZonedDateTime expirationDateTime = ZonedDateTime.now(zone).plusHours(1);

		long expirationEpochMilli = expirationDateTime.toInstant().toEpochMilli();

		Cookie cookie = new Cookie("t", jwt);
		cookie.setPath("/");
		cookie.setAttribute("Expires", expirationDateTime.toString());
		cookie.setMaxAge((int) ((expirationEpochMilli - ZonedDateTime.now(zone).toInstant().toEpochMilli()) / 1000));
		cookie.setHttpOnly(true);

		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getResponse();
		response.addCookie(cookie);

		return new ResponseEntity<String>(jwt, HttpStatus.OK);
	}

	@GetMapping(SecurityControllerUrls.GET_PUBLIC_KEY)
	public ResponseEntity<String> getPublicKey() {
		byte[] publicKeyBytes = storePublicKey.getEncoded();

		String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKeyBytes);

		StringBuilder pemFormat = new StringBuilder();
		pemFormat.append("-----BEGIN PUBLIC KEY-----\n");
		pemFormat.append(publicKeyBase64);
		pemFormat.append("\n-----END PUBLIC KEY-----");
		return ResponseEntity.ok(pemFormat.toString());
	}

	@GetMapping(SecurityControllerUrls.GET_USER_PAYLOAD_FROM_TOKEN)
	public ResponseEntity getTokenPayloadFromHeaders(HttpServletRequest request) throws JsonProcessingException {
		return ResponseEntity.ok(jwtHelper.parsePayloadFromJwt(request.getHeader("authorization")));

	}
	
	@GetMapping(SecurityControllerUrls.GET_TOKEN_FOR_LINK)
	public ResponseEntity<?> getVerificationTokenForLink(@RequestParam(name = "email", required = true) String userMail) {
		return ResponseEntity.ok(jwtHelper.createJwtForClaims(userMail,  Map.of() , 5));
	}
}
