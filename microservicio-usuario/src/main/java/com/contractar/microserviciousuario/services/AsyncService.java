package com.contractar.microserviciousuario.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciocommons.constants.controllers.AdminControllerUrls;
import com.contractar.microserviciocommons.dto.usuario.sensibleinfo.UsuarioActiveDTO;
import com.contractar.microserviciocommons.exceptions.UserCreationException;

@Service
public class AsyncService {
	@Value("${microservicio-usuario.url}")
	private String microservicioUsuarioUrl;
	
	private RestTemplate httpClient;

	public AsyncService(RestTemplate httpClient) {
		this.httpClient = httpClient;
	}

	@Async
	public void requestUsuarioActiveFlag(Long userId) throws UserCreationException {
		String url = microservicioUsuarioUrl + AdminControllerUrls.ADMIN_USUARIOS_ACTIVE;
		try {
			httpClient.postForEntity(url, new UsuarioActiveDTO(userId, true), Void.class);
		} catch (RestClientException e) {
			throw new UserCreationException();
		}
	}
}
