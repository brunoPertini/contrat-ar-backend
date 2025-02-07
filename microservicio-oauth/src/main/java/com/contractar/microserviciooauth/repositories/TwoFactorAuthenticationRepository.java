package com.contractar.microserviciooauth.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.contractar.microserviciooauth.models.TwoFactorAuthenticationRecord;

@Repository
public interface TwoFactorAuthenticationRepository extends CrudRepository<TwoFactorAuthenticationRecord, Long> {
	
	@SuppressWarnings("unchecked")
	public TwoFactorAuthenticationRecord save(TwoFactorAuthenticationRecord r);

	public Optional<TwoFactorAuthenticationRecord> findTopByUserIdOrderByCreationDateTimeDesc(Long userId);

}
