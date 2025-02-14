package com.contractar.microserviciooauth.services;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciocommons.constants.controllers.SecurityControllerUrls;
import com.contractar.microserviciocommons.exceptions.SessionExpiredException;
import com.contractar.microserviciocommons.mailing.TwoFactorAuthMailInfo;
import com.contractar.microserviciooauth.dtos.Create2FaRecordResponse;
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

	// 2fa code ttl
	private static final int EXPIRES_IN_MINUTES = 1;
	
	//2fa successful verification ttl in minutes
	private static final int SUSSCESSFUL_CHECK_TTL = 5;

	@Value("${microservicio-mailing.url}")
	private String malilingServiceUrl;
	
	@Value("${microservicio-config.url}")
	private String configServiceUrl;

	public TwoFactorAuthenticationService(RestTemplate httpClient, JwtHelper jwtHelper,
			TwoFactorAuthenticationRepository repository) {
		this.httpClient = httpClient;
		this.jwtHelper = jwtHelper;
		this.repository = repository;
		this.secureRandom = new SecureRandom();
	}
	
	private String getMessageTag(String tagId) {
		final String fullUrl = configServiceUrl + "/i18n/" + tagId;
		return httpClient.getForObject(fullUrl, String.class);
	}
	
	public boolean isUser2faStillValid(String jwt) throws JsonProcessingException, SessionExpiredException {
		Map<String, Object> tokenPayload = (Map<String, Object>) jwtHelper.parsePayloadFromJwt(jwt);

		Long userId = Long.valueOf((String) tokenPayload.get("id"));

		Optional<TwoFactorAuthenticationRecord> recordOpt = repository
				.findTopByUserIdOrderByCreationDateTimeDesc(userId);
		
		return recordOpt.map((lastRecord) -> {
			return (!Instant.now().isAfter(lastRecord.getCreationDateTime().plus(SUSSCESSFUL_CHECK_TTL, ChronoUnit.MINUTES)) && 
					(lastRecord.getResult().equals(TwoFactorAuthResult.PASSED)));
		}).orElseGet(() -> false);
	}

	private void send2FaEmaiil(TwoFactorAuthenticationRecord newRecord, String userEmail, String userFullName) {
		TwoFactorAuthMailInfo body = new TwoFactorAuthMailInfo(userEmail, newRecord.getCode(), EXPIRES_IN_MINUTES,
				userFullName);

		String url = malilingServiceUrl + SecurityControllerUrls.SEND_2FA_MAIL;

		HttpEntity<TwoFactorAuthMailInfo> entity = new HttpEntity<>(body);

		httpClient.exchange(url, HttpMethod.POST, entity, Void.class);

	}

	private Create2FaRecordResponse saveNewRecordForUser(Long userId, String userEmail, String userFullName) {
		int generatedCode = secureRandom.nextInt(1000) + 1;
		TwoFactorAuthenticationRecord newRecord = new TwoFactorAuthenticationRecord(userId, generatedCode,
				Instant.now(), TwoFactorAuthResult.PENDING);

		repository.save(newRecord);
		send2FaEmaiil(newRecord, userEmail, userFullName);

		int codeLength = String.valueOf(generatedCode).length();
		return new Create2FaRecordResponse(EXPIRES_IN_MINUTES, codeLength); 
	}

	private boolean isCodeExpired(TwoFactorAuthenticationRecord twoFaRecord) {
		Instant recordCreationTime = twoFaRecord.getCreationDateTime();
		return Instant.now().isAfter(recordCreationTime.plus(EXPIRES_IN_MINUTES, ChronoUnit.MINUTES));
	}

	/**
	 * 
	 * @param jwt
	 * @return The created code length and code's ttl
	 * @throws JsonProcessingException
	 * @throws SessionExpiredException
	 */
	public Create2FaRecordResponse saveRecordForUser(String jwt) throws JsonProcessingException, SessionExpiredException {
		Map<String, Object> tokenPayload = (Map<String, Object>) jwtHelper.parsePayloadFromJwt(jwt);
		
		final Function<String, String> parseValueToString = (String key) -> (String) tokenPayload.get(key);

		Long userId = Long.valueOf(parseValueToString.apply("id"));
		String email = parseValueToString.apply("sub");
		String name = parseValueToString.apply("name");
		String surname = parseValueToString.apply("surname");
		String fullName = name + " " + surname;

		Optional<TwoFactorAuthenticationRecord> recordOpt = repository
				.findTopByUserIdOrderByCreationDateTimeDesc(userId);

		return recordOpt.map(twoFaRecord -> {
			boolean codeIsExpired = isCodeExpired(twoFaRecord);
			
			if (!twoFaRecord.wasChecked()) {
				if (codeIsExpired) {
					return saveNewRecordForUser(userId, email, fullName);
				}
				
				throw new TwoFaCodeAlreadySendException(getMessageTag("exceptions.2fa.alreadySend"));
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
			throw new CodeWasntRequestedException(getMessageTag("exceptions.2fa.noCodeRequested"));
		}

		TwoFactorAuthenticationRecord lastRecord = recordOpt.get();

		if (lastRecord.wasChecked()) {
			throw new CodeWasAlreadyApplied(getMessageTag("exceptions.2fa.codeAlreadyApplied"));
		}

		TwoFactorAuthResult newResult;

		if (isCodeExpired(lastRecord)) {
			newResult = TwoFactorAuthResult.EXPIRED;
		} else 	if (lastRecord.getCode() == code) {
			newResult = TwoFactorAuthResult.PASSED;
		} else {
			newResult = TwoFactorAuthResult.FAILED;
		}

		lastRecord.setResult(newResult);
		repository.save(lastRecord);
		return newResult;
	}
}
