package com.contractar.microserviciooauth.helpers;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Component;

@Component
public class JwtHelper {
	
	private final RSAPrivateKey privateKey;
	private final RSAPublicKey publicKey;
	
	public JwtHelper(RSAPrivateKey storePrivateKey, RSAPublicKey storePublicKey) {
		this.privateKey = storePrivateKey;
		this.publicKey = storePublicKey;
	}
	
	public String createJwtForClaims(String subject, Map<String, Object> claims) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(Instant.now().toEpochMilli());
		calendar.add(Calendar.MINUTE, 40);
		
		JWTCreator.Builder jwtBuilder = JWT.create().withSubject(subject);
		
		// Add claims
		claims.forEach((key, value) -> {
			if (value instanceof String) {
				jwtBuilder.withClaim(key, (String)value);
			} else {
				jwtBuilder.withArrayClaim(key, (String[]) value);
			}
		});
		
		// Add expiredAt and etc
		return jwtBuilder
				.withNotBefore(new Date())
				.withExpiresAt(calendar.getTime())
				.sign(Algorithm.RSA256(publicKey, privateKey));
	}
}
