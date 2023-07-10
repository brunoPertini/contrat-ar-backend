package com.contractar.microserviciooauth.services;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
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

			List<GrantedAuthority> authorities = new ArrayList<>();
			
			authorities.add(new SimpleGrantedAuthority(user.getRole().getNombre()));

			return new User(user.getEmail(), user.getPassword(), authorities);

		} catch(Exception e) {
			throw e;
		}
		
	}
	
	public String createJwtForUser(String email, String password, UserDetails userDetails) throws UserNotFoundException {
		if (passwordEncoder.matches(password, userDetails.getPassword())) {
			Map<String, Object> claims = new HashMap<>();
			String userRole = userDetails.getAuthorities().stream().findFirst().get().toString();
			claims.put("role", userRole);
			claims.put("indexPage", IndexPagesRoutes.getAllRoutes().get(userRole));
			return jwtHelper.createJwtForClaims(email, claims);
		}
		
		throw new UserNotFoundException();
	}

}
