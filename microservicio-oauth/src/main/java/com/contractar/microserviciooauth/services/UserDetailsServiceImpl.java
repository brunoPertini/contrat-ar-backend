package com.contractar.microserviciooauth.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.contractar.microserviciocommons.constants.IndexPagesRoutes;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciooauth.helpers.JwtHelper;
import com.contractar.microserviciousuario.dtos.UsuarioOauthDTO;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

	@Value("${security.users.usersServiceUrl}")
	private String usersPath;

	private RestTemplate httpClient;

	private final JwtHelper jwtHelper;
	private final PasswordEncoder passwordEncoder;

	public UserDetailsServiceImpl(RestTemplate restTemplate, JwtHelper jwtHelper, PasswordEncoder passwordEncoder) {
		this.httpClient = restTemplate;
		this.jwtHelper = jwtHelper;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public UserDetails loadUserByUsername(String email) {				
		UriComponentsBuilder usersByEmailUrlBuilder = UriComponentsBuilder.fromHttpUrl(usersPath)
				.queryParam("email", email)
				.queryParam("checkIfInactive", "true");

		UsuarioOauthDTO user = httpClient.getForObject(usersByEmailUrlBuilder.toUriString(), UsuarioOauthDTO.class);

		return user;

	}
	
	public UserDetails loadUserByEmail(String email, boolean checkIfInactive) {				
		UriComponentsBuilder usersByEmailUrlBuilder = UriComponentsBuilder.fromHttpUrl(usersPath)
				.queryParam("email", email)
				.queryParam("checkIfInactive", String.valueOf(checkIfInactive))
				.encode();

		UsuarioOauthDTO user = httpClient.getForObject(usersByEmailUrlBuilder.toUriString(), UsuarioOauthDTO.class);

		return user;

	}

	public String createJwtForUser(String email, String password, UsuarioOauthDTO userDetails,
			List<SimpleGrantedAuthority> authorities) throws UserNotFoundException {
		if (passwordEncoder.matches(password, userDetails.getPassword())) {
			Map<String, Object> claims = new HashMap<>();
			String userRole = userDetails.getRole().getNombre();
			String indexPage = Optional.ofNullable(IndexPagesRoutes.getAllRoutes().get(userRole))
					.orElseGet(() -> IndexPagesRoutes.getAllRoutes().get("DEFAULT"));
			claims.put("id", userDetails.getId());
			claims.put("role", userRole);
			claims.put("name", userDetails.getName());
			claims.put("surname", userDetails.getSurname());
			claims.put("indexPage", indexPage);
			claims.put("authorities", authorities);
			return jwtHelper.createJwtForClaims(email, claims, 40);
		}

		throw new UserNotFoundException();
	}

}
