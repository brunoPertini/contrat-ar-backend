package com.contractar.microserviciooauth.helpers;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtHelper {

	private final RSAPrivateKey privateKey;
	private final RSAPublicKey publicKey;

	public JwtHelper(RSAPrivateKey storePrivateKey, RSAPublicKey storePublicKey) {
		this.privateKey = storePrivateKey;
		this.publicKey = storePublicKey;
	}

	public String createJwtForClaims(String subject, Map<String, Object> claims, int expiresInMinutes) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(Instant.now().toEpochMilli());
		calendar.add(Calendar.MINUTE, expiresInMinutes);

		JWTCreator.Builder jwtBuilder = JWT.create().withSubject(subject);

		// Add claims
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
	public Object parsePayloadFromJwt(String jwtToken) throws JsonProcessingException {
		Algorithm algorithm = Algorithm.RSA256(publicKey);
		JWTVerifier verifier = JWT.require(algorithm).build();

		DecodedJWT decodedJWT = verifier.verify(jwtToken.replace("Bearer ", ""));

		String payload = decodedJWT.getPayload();

		ObjectMapper objectMapper = new ObjectMapper();

		byte[] decodedPayloadBytes = Base64.getDecoder().decode(payload);
		String decodedPayloadString = new String(decodedPayloadBytes);

		return objectMapper.readValue(decodedPayloadString, Object.class);
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
}
