package com.contractar.microserviciooauth.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.contractar.microserviciooauth.models.TwoFactorAuthenticationRecord;

public interface TwoFactorAuthenticationRepository extends CrudRepository<TwoFactorAuthenticationRecord, Long> {
	public Optional<TwoFactorAuthenticationRecord> findTopByUserIdOrderByCreationDateTimeDesc(Long userId);

}
