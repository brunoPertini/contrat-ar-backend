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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.contractar.microserviciocommons.constants.controllers.SecurityControllerUrls;
import com.contractar.microserviciocommons.dto.TokenInfoPayload;
import com.contractar.microserviciocommons.dto.TokenType;
import com.contractar.microserviciocommons.exceptions.SessionExpiredException;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciooauth.exceptions.CodeWasAlreadyApplied;
import com.contractar.microserviciooauth.exceptions.CodeWasntRequestedException;
import com.contractar.microserviciooauth.helpers.JwtHelper;
import com.contractar.microserviciooauth.services.TwoFactorAuthenticationService;
import com.contractar.microserviciooauth.services.UserDetailsServiceImpl;
import com.contractar.microserviciousuario.dtos.UsuarioOauthDTO;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
public class SecurityController {

	private UserDetailsService userDetailsService;

	private TwoFactorAuthenticationService twoFactorAuthenticationService;

	@Autowired
	private RSAPublicKey storePublicKey;

	@Autowired
	private JwtHelper jwtHelper;

	public SecurityController(UserDetailsService userDetailsService,
			TwoFactorAuthenticationService twoFactorAuthenticationService) {
		this.userDetailsService = userDetailsService;
		this.twoFactorAuthenticationService = twoFactorAuthenticationService;
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
	public ResponseEntity<?> getTokenPayloadFromHeaders(HttpServletRequest request,
			@RequestParam(required = false) boolean verifyToken)
			throws JsonProcessingException, SessionExpiredException {
		String token = request.getHeader("authorization");
		return ResponseEntity.ok(
				!verifyToken ? jwtHelper.parsePayloadFromUnverifiedToken(token) : jwtHelper.parsePayloadFromJwt(token));

	}

	@GetMapping(SecurityControllerUrls.GET_TOKEN_FOR_LINK)
	public ResponseEntity<?> getVerificationTokenForLink(
			@RequestParam(name = "email", required = true) String userMail) {
		return ResponseEntity.ok(jwtHelper.createJwtForClaims(userMail, Map.of(), 5));
	}

	@GetMapping(SecurityControllerUrls.GET_TOKEN_FOR_NEW_USER)
	public ResponseEntity<?> getVerificationTokenForNewUser(
			@RequestParam(name = "email", required = true) String userMail,
			@RequestParam(name = "userId", required = true) Long userId) {

		UsuarioOauthDTO createdUser = (UsuarioOauthDTO) ((UserDetailsServiceImpl) userDetailsService)
				.loadUserByEmail(userMail, false);

		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(createdUser.getRole().getNombre());
		List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>(
				Collections.singletonList(authority));

		return ResponseEntity
				.ok(jwtHelper.createJwtForClaims(userMail, Map.of("id", userId, "authorities", authorities), 10));
	}

	@GetMapping(SecurityControllerUrls.TOKEN_BASE_PATH)
	public ResponseEntity<Boolean> verifyToken(@RequestParam(required = true) String token) {
		boolean result = jwtHelper.verifyToken(token);
		return new ResponseEntity<Boolean>(result, HttpStatus.OK);
	}

	@GetMapping(SecurityControllerUrls.TOKEN_BASE_PATH_V2)
	public ResponseEntity<Boolean> verifyTokenV2(HttpServletRequest request,
			@RequestParam(required = true) TokenType type) {
		String token = request.getHeader("Authorization").replace("Bearer ", "");
		return new ResponseEntity<Boolean>(jwtHelper.verifyTokenV2(token, type), HttpStatus.OK);
	}

	@PostMapping(SecurityControllerUrls.TOKEN_BASE_PATH)
	public ResponseEntity<String> createToken(@RequestBody @Valid TokenInfoPayload payload) {
		return ResponseEntity.ok(jwtHelper.createJwtForClaims(payload));
	}

	@GetMapping(SecurityControllerUrls.CHECK_USER_2FA)
	public ResponseEntity<Boolean> checkUser2faRecord(@PathVariable(required = true) String jwt)
			throws JsonProcessingException, SessionExpiredException {
		return new ResponseEntity<Boolean>(twoFactorAuthenticationService.isUser2faStillValid(jwt), HttpStatus.OK);
	}

	@PostMapping(SecurityControllerUrls.SEND_2FA_MAIL)
	public ResponseEntity<?> requestTwoFactorAuthentication(HttpServletRequest request)
			throws JsonProcessingException, SessionExpiredException {
		return new ResponseEntity<>(
				twoFactorAuthenticationService.saveRecordForUser(request.getHeader("Authorization")),
				HttpStatus.CREATED);
	}

	@PatchMapping(SecurityControllerUrls.SEND_2FA_MAIL_CONFIRM)
	public ResponseEntity<?> confirmTwoFactorAuthentication(@PathVariable @NotNull Integer code,
			HttpServletRequest request) throws JsonProcessingException, CodeWasntRequestedException,
			CodeWasAlreadyApplied, SessionExpiredException {
		return new ResponseEntity<>(
				twoFactorAuthenticationService.confirmTwoFactorAuthentication(request.getHeader("Authorization"), code),
				HttpStatus.OK);
	}
}
