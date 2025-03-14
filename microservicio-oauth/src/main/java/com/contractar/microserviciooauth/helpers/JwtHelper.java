package com.contractar.microserviciooauth.helpers;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.contractar.microserviciocommons.dto.TokenInfoPayload;
import com.contractar.microserviciocommons.dto.TokenType;
import com.contractar.microserviciocommons.exceptions.SessionExpiredException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class JwtHelper {

	// Duration in minutes
	private static int FORGOT_PASSWORD_TOKEN_DURATION = 5;

	private final RSAPrivateKey privateKey;
	private final RSAPublicKey publicKey;

	private RestTemplate httpClient;

	@Value("${microservicio-config.url}")
	private String configServiceUrl;

	public JwtHelper(RSAPrivateKey storePrivateKey, RSAPublicKey storePublicKey, RestTemplate httpClient) {
		this.privateKey = storePrivateKey;
		this.publicKey = storePublicKey;
		this.httpClient = httpClient;
	}

	private String getMessageTag(String tagId) {
		final String fullUrl = configServiceUrl + "/i18n/" + tagId;
		return httpClient.getForObject(fullUrl, String.class);
	}

	public String createJwtForClaims(TokenInfoPayload payload) {
		Map<String, Object> parsedClaims = new HashMap<>();
		parsedClaims.put("type", payload.getType());
		parsedClaims.put("id", payload.getUserId());
		parsedClaims.put("role", payload.getRoleName());
		parsedClaims.put("authorities", List.of(new SimpleGrantedAuthority(payload.getRoleName())));

		return this.createJwtForClaims(payload.getSub(), parsedClaims, FORGOT_PASSWORD_TOKEN_DURATION);

	}

	public String createJwtForClaims(String subject, Map<String, Object> claims, int expiresInMinutes) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(Instant.now().toEpochMilli());
		calendar.add(Calendar.MINUTE, expiresInMinutes);

		JWTCreator.Builder jwtBuilder = JWT.create().withSubject(subject);

		claims.forEach((key, value) -> {
			if (value instanceof String) {
				jwtBuilder.withClaim(key, (String) value);
			} else if (key.equals("authorities")) {
				List<SimpleGrantedAuthority> authorities = (List<SimpleGrantedAuthority>) value;
				String[] authoritiesArray = new String[authorities.size()];
				for (int i = 0; i < authorities.size(); ++i) {
					authoritiesArray[i] = authorities.get(i).toString();
				}
				jwtBuilder.withArrayClaim(key, authoritiesArray);
			} else {

				jwtBuilder.withClaim(key, value.toString());
			}
		});

		// Add expiredAt and etc
		return jwtBuilder.withNotBefore(new Date()).withExpiresAt(calendar.getTime())
				.sign(Algorithm.RSA256(publicKey, privateKey));
	}

	@SuppressWarnings("unchecked")
	public Object parsePayloadFromJwt(String jwtToken) throws JsonProcessingException, SessionExpiredException {
		Algorithm algorithm = Algorithm.RSA256(publicKey);
		JWTVerifier verifier = JWT.require(algorithm).build();

		try {
			DecodedJWT decodedJWT = verifier.verify(jwtToken.replace("Bearer ", ""));

			String payload = decodedJWT.getPayload();

			ObjectMapper objectMapper = new ObjectMapper();

			byte[] decodedPayloadBytes = Base64.getDecoder().decode(payload);
			String decodedPayloadString = new String(decodedPayloadBytes);

			return objectMapper.readValue(decodedPayloadString, Object.class);
		} catch (JWTVerificationException ex) {
			throw new SessionExpiredException(getMessageTag("exceptions.session.expried"));
		}
	}

	public Object parsePayloadFromUnverifiedToken(String jwtToken)
			throws JsonMappingException, JsonProcessingException {
		DecodedJWT decodedJWT = JWT.decode(jwtToken.replace("Bearer ", ""));
		String decodedPayload = new String(java.util.Base64.getUrlDecoder().decode(decodedJWT.getPayload()));
		return new ObjectMapper().readValue(decodedPayload, Object.class);
	}

	public boolean verifyToken(String token) {
		Algorithm algorithm = Algorithm.RSA256(publicKey);
		JWTVerifier verifier = JWT.require(algorithm).build();

		try {
			verifier.verify(token);
			return true;

		} catch (JWTVerificationException e) {
			return false;
		}

	}

	public boolean verifyTokenV2(String token, TokenType expectedType) {
		Algorithm algorithm = Algorithm.RSA256(publicKey);
		JWTVerifier verifier = JWT.require(algorithm).build();

		try {
			DecodedJWT decodedJWT = verifier.verify(token);

			String tokenTypeClaim = decodedJWT.getClaim("type").asString();

			if (tokenTypeClaim == null || !expectedType.name().equals(tokenTypeClaim)) {
				return false;
			}

			return true;

		} catch (JWTVerificationException e) {
			return false;
		}
	}
}
