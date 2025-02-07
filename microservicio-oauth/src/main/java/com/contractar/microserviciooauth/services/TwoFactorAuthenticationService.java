package com.contractar.microserviciooauth.services;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciooauth.exceptions.TwoFaCodeAlreadySendException;
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

	private static final int EXPIRES_IN_MINUTES = 1;

	public TwoFactorAuthenticationService(RestTemplate httpClient, JwtHelper jwtHelper,
			TwoFactorAuthenticationRepository repository) {
		this.httpClient = httpClient;
		this.jwtHelper = jwtHelper;
		this.repository = repository;
		this.secureRandom = new SecureRandom();
	}

	private int saveNewRecordForUser(Long userId) {
		int generatedCode = secureRandom.nextInt();
		TwoFactorAuthenticationRecord newRecord = new TwoFactorAuthenticationRecord(userId, generatedCode,
				Instant.now(), false);

		repository.save(newRecord);
		// Enviar email
		
		return String.valueOf(generatedCode).length();
	}

	/**
	 * 
	 * @param jwt
	 * @return The created code length
	 * @throws JsonProcessingException
	 */
	public int saveRecordForUser(String jwt) throws JsonProcessingException {
		Map<String, Object> tokenPayload = (Map<String, Object>) jwtHelper.parsePayloadFromJwt(jwt);
		
		Long userId = (Long) tokenPayload.get("id");
		
		Optional<TwoFactorAuthenticationRecord> recordOpt = repository.findTopByUserIdOrderByCreationDateTimeDesc(userId);
		
		return recordOpt.map(twoFaRecord -> {
			Instant recordCreationTime = twoFaRecord.getCreationDateTime();
			boolean codeIsExpired = recordCreationTime.plus(EXPIRES_IN_MINUTES, ChronoUnit.MINUTES).isAfter(Instant.now());
			
			if (codeIsExpired && !twoFaRecord.isWasChecked()) {
				return saveNewRecordForUser(userId);
			}
			
			if (!codeIsExpired && !twoFaRecord.isWasChecked()) {
				throw new TwoFaCodeAlreadySendException("¡El código ya fue enviado! Podrás reenviarlo en unos minutos");
			}
			
			return saveNewRecordForUser(userId);
			
		}).orElseGet(() -> saveNewRecordForUser(userId));
		
		
	}
}
