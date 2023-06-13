package com.contractar.microserviciogateway.security;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.stereotype.Component;

@Component("securityUtils")
public class SecurityUtils {

	@Value("${spring.security.oauth2.client.id}")
	private String clientId;

	@Value("${spring.security.oauth2.client.secret}")
	private String clientSecret;

	public boolean hasValidClientId(HttpServletRequest request) {
		String clientId = request.getHeader("client-id");
		String clientSecret = request.getHeader("client-secret");

		boolean isValidClient = Optional.ofNullable(clientId).flatMap(cId -> Optional.ofNullable(clientSecret)
				.map(cSecret -> this.clientId.equals(cId) && this.clientSecret.equals(cSecret))).orElse(false);

		if (isValidClient) {
			return true;
		} else {
			throw new OAuth2AuthenticationException(new OAuth2Error("Invalid client"));
		}

	}
}
