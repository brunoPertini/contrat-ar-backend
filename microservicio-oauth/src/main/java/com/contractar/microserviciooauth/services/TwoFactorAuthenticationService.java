package com.contractar.microserviciooauth.services;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciocommons.constants.controllers.SecurityControllerUrls;
import com.contractar.microserviciocommons.exceptions.SessionExpiredException;
import com.contractar.microserviciocommons.mailing.TwoFactorAuthMailInfo;
import com.contractar.microserviciooauth.exceptions.CodeWasAlreadyApplied;
import com.contractar.microserviciooauth.exceptions.CodeWasntRequestedException;
import com.contractar.microserviciooauth.exceptions.TwoFaCodeAlreadySendException;
import com.contractar.microserviciooauth.helpers.JwtHelper;
import com.contractar.microserviciooauth.models.TwoFactorAuthResult;
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

	@Value("${microservicio-mailing.url}")
	private String malilingServiceUrl;

	public TwoFactorAuthenticationService(RestTemplate httpClient, JwtHelper jwtHelper,
			TwoFactorAuthenticationRepository repository) {
		this.httpClient = httpClient;
		this.jwtHelper = jwtHelper;
		this.repository = repository;
		this.secureRandom = new SecureRandom();
	}

	private void send2FaEmaiil(TwoFactorAuthenticationRecord newRecord, String userEmail, String userFullName) {
		TwoFactorAuthMailInfo body = new TwoFactorAuthMailInfo(userEmail, newRecord.getCode(), EXPIRES_IN_MINUTES,
				userFullName);

		String url = malilingServiceUrl + SecurityControllerUrls.SEND_2FA_MAIL;

		HttpEntity<TwoFactorAuthMailInfo> entity = new HttpEntity<>(body);

		httpClient.exchange(url, HttpMethod.POST, entity, Void.class);

	}

	private int saveNewRecordForUser(Long userId, String userEmail, String userFullName) {
		int generatedCode = secureRandom.nextInt(1000) + 1;
		TwoFactorAuthenticationRecord newRecord = new TwoFactorAuthenticationRecord(userId, generatedCode,
				Instant.now(), TwoFactorAuthResult.PENDING);

		repository.save(newRecord);
		send2FaEmaiil(newRecord, userEmail, userFullName);

		return String.valueOf(generatedCode).length();
	}

	private boolean isCodeExpired(TwoFactorAuthenticationRecord twoFaRecord) {
		Instant recordCreationTime = twoFaRecord.getCreationDateTime();
		return Instant.now().isAfter(recordCreationTime.plus(EXPIRES_IN_MINUTES, ChronoUnit.MINUTES));
	}

	/**
	 * 
	 * @param jwt
	 * @return The created code length
	 * @throws JsonProcessingException
	 * @throws SessionExpiredException
	 */
	public int saveRecordForUser(String jwt) throws JsonProcessingException, SessionExpiredException {
		Map<String, Object> tokenPayload = (Map<String, Object>) jwtHelper.parsePayloadFromJwt(jwt);

		Long userId = Long.valueOf((String) tokenPayload.get("id"));
		String email = (String) tokenPayload.get("sub");
		String name = (String) tokenPayload.get("name");
		String surname = (String) tokenPayload.get("surname");
		String fullName = name + " " + surname;

		Optional<TwoFactorAuthenticationRecord> recordOpt = repository
				.findTopByUserIdOrderByCreationDateTimeDesc(userId);

		return recordOpt.map(twoFaRecord -> {
			boolean codeIsExpired = isCodeExpired(twoFaRecord);

			if (codeIsExpired && !twoFaRecord.wasChecked()) {
				return saveNewRecordForUser(userId, email, fullName);
			}

			if (!codeIsExpired && !twoFaRecord.wasChecked()) {
				throw new TwoFaCodeAlreadySendException("¡El código ya fue enviado! Podrás reenviarlo en unos minutos");
			}

			return saveNewRecordForUser(userId, email, fullName);

		}).orElseGet(() -> saveNewRecordForUser(userId, email, fullName));

	}

	public TwoFactorAuthResult confirmTwoFactorAuthentication(String jwt, int code) throws JsonProcessingException,
			CodeWasntRequestedException, CodeWasAlreadyApplied, SessionExpiredException {
		Map<String, Object> tokenPayload = (Map<String, Object>) jwtHelper.parsePayloadFromJwt(jwt);

		Long userId = Long.valueOf((String) tokenPayload.get("id"));

		Optional<TwoFactorAuthenticationRecord> recordOpt = repository
				.findTopByUserIdOrderByCreationDateTimeDesc(userId);

		if (recordOpt.isEmpty()) {
			throw new CodeWasntRequestedException("¡No solicitaste ningún código!");
		}

		TwoFactorAuthenticationRecord lastRecord = recordOpt.get();

		if (lastRecord.wasChecked()) {
			throw new CodeWasAlreadyApplied("¡El código ya fue aplicado!");
		}

		TwoFactorAuthResult newResult;

		if (isCodeExpired(lastRecord)) {
			newResult = TwoFactorAuthResult.EXPIRED;
		}

		if (lastRecord.getCode() == code) {
			newResult = TwoFactorAuthResult.PASSED;
		} else {
			newResult = TwoFactorAuthResult.FAILED;
		}

		lastRecord.setResult(newResult);
		repository.save(lastRecord);
		return newResult;
	}
}
