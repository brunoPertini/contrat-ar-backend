package com.contractar.microserviciosecurity.services;

import java.util.List;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciousuario.models.Usuario;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {
	@Value("${routes.microservicio-usuario.url}")
	private final String usersBaseUrl;

	private final String usersPath = "/usuarios";
	
	private RestTemplate httpClient;

	public UserDetailsServiceImpl(RestTemplate restTemplate) {
		this.usersBaseUrl = "";
		this.httpClient = restTemplate;		
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		Map<String, String> parameters = Map.ofEntries(
				new AbstractMap.SimpleEntry<String, String>("email", email));
		
		Usuario user = httpClient.getForObject(usersPath, Usuario.class, parameters);

		List<GrantedAuthority> authorities = new ArrayList<>();
		
		authorities.add(new SimpleGrantedAuthority("USER"));

		return new User(user.getEmail(), user.getPassword(), authorities);

	}

}
