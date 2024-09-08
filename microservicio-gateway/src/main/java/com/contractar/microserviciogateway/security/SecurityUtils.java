package com.contractar.microserviciogateway.security;

import java.util.LinkedHashMap;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciocommons.constants.controllers.SecurityControllerUrls;

@Component("securityUtils")
public class SecurityUtils {

	@Value("${spring.security.oauth2.client.id}")
	private String clientId;

	@Value("${spring.security.oauth2.client.secret}")
	private String clientSecret;

	@Value("${zuul.routes.microservicio-oauth.url}")
	private String SERVICIO_SECURITY_URL;

	@Autowired
	private RestTemplate restTemplate;

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


	/**
	 * Checks if the userId in the request's jwt matches with the userId that comes after 
	 * the passed path variable section
	 */
	public boolean userIdsMatch(HttpServletRequest request, String pathSection) {
		String getPayloadUrl = SERVICIO_SECURITY_URL + SecurityControllerUrls.GET_USER_PAYLOAD_FROM_TOKEN;

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", request.getHeader("Authorization"));

		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<LinkedHashMap> getClientIdResponse = restTemplate.exchange(getPayloadUrl, HttpMethod.GET, entity,
				LinkedHashMap.class);

		Long idFromToken = Long.parseLong((String)getClientIdResponse.getBody().get("id"));

		String pathInfo = request.getServletPath();

		if (pathInfo == null || idFromToken == null) {
			return false;
		}
		String[] pathParts = pathInfo.split("/");

		Long idFromRequest = null;

		for (int i = 0; i < pathParts.length; ++i) {
			if (pathParts[i].equals(pathSection)) {
				idFromRequest = Long.valueOf(pathParts[i + 1]);
			}
		}

		return idFromRequest != null && idFromToken.equals(idFromRequest);

	}
}