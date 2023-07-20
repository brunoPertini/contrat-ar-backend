package com.contractar.microserviciooauth.services;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciocommons.constants.IndexPagesRoutes;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciooauth.helpers.JwtHelper;
import com.contractar.microserviciousuario.models.Usuario;

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
		try {
			Map<String, String> parameters = Map.ofEntries(
					new AbstractMap.SimpleEntry<String, String>("email", email));
			
			Usuario user = httpClient.getForObject(usersPath, Usuario.class, parameters);

			return user;

		} catch(Exception e) {
			throw e;
		}
		
	}
	
	public String createJwtForUser(String email, String password, Usuario userDetails) throws UserNotFoundException {
		if (passwordEncoder.matches(password, userDetails.getPassword())) {
			Map<String, Object> claims = new HashMap<>();
			String userRole = userDetails.getRole().getNombre();
			String indexPage = Optional.ofNullable(IndexPagesRoutes.getAllRoutes().get(userRole))
			.orElseGet(() -> IndexPagesRoutes.getAllRoutes().get("DEFAULT"));		
			claims.put("role", userRole);
			claims.put("name", userDetails.getname());
			claims.put("surname", userDetails.getsurname());
			claims.put("indexPage", indexPage);
			return jwtHelper.createJwtForClaims(email, claims);
		}
		
		throw new UserNotFoundException();
	}

}
