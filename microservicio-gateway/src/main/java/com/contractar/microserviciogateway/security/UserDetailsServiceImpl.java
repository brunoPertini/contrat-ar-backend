package com.contractar.microserviciogateway.security;

import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.contractar.microserviciousuario.models.Usuario;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {
	@Value("${zuul.routes.microservicio-usuario.url}")
	private final String usersBaseUrl;

	private final String usersPath = "/usuarios";

	public UserDetailsServiceImpl() {
		this.usersBaseUrl = "";
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		WebClient client = WebClient.builder().baseUrl(usersBaseUrl)
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).build();

		Usuario user = client.get().uri(uriBuilder -> uriBuilder.path(usersPath).queryParam("email", email).build())
				.retrieve().bodyToMono(Usuario.class).block();
		

		List<GrantedAuthority> authorities = new ArrayList<>();
		
		authorities.add(new SimpleGrantedAuthority("USER"));

		return new User(user.getEmail(), user.getPassword(), authorities);

	}

}
