package com.contractar.microserviciooauth.services;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciooauth.helpers.JwtHelper;
import com.contractar.microserviciooauth.models.TwoFactorAuthenticationRecord;
import com.contractar.microserviciooauth.repositories.TwoFactorAuthenticationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class TwoFactorAuthenticationService {
	private RestTemplate httpClient;
	
	private JwtHelper jwtHelper;
	
	private TwoFactorAuthenticationRepository repository;
	
	private final SecureRandom secureRandom;
	
	public TwoFactorAuthenticationService(RestTemplate httpClient, JwtHelper jwtHelper, TwoFactorAuthenticationRepository repository) {
		this.httpClient = httpClient;
		this.jwtHelper = jwtHelper;
		this.repository = repository;
		this.secureRandom = new SecureRandom();
	}
	
	private void saveNewRecordForUser(Long userId) {
		TwoFactorAuthenticationRecord newRecord = new TwoFactorAuthenticationRecord(userId, secureRandom.nextInt())
	}

	public void saveRecordForUser(String jwt) throws JsonProcessingException {
		Map<String, Object> tokenPayload = (Map<String, Object>) jwtHelper.parsePayloadFromJwt(jwt);
		
		Long userId = (Long) tokenPayload.get("id");
		
		Optional<TwoFactorAuthenticationRecord> recordOpt = repository.findTopByUserIdOrderByCreationDateTimeDesc(userId);
		
		recordOpt.ifPresentOrElse((twoFaRecord -> {
			
		}), null);
	}
}
